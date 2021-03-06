/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.config;

import java.util.List;
import java.util.Set;

import org.ocpsoft.rewrite.bind.Evaluation;
import org.ocpsoft.rewrite.param.DefaultParameter;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.Parameterized;

/**
 * An intermediate stage {@link Rule} configuration.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ConfigurationRuleBuilder extends ConfigurationBuilder implements
         ConfigurationRuleBuilderCustom,
         ConfigurationRuleBuilderWhen,
         ConfigurationRuleBuilderPerform,
         ConfigurationRuleBuilderOtherwise,
         ConfigurationRuleBuilderWithId,
         ConfigurationRuleBuilderWithPriority,
         ConfigurationRuleBuilderWithPriorityAndId
{
   private final ConfigurationBuilder wrapped;
   private final RuleBuilder rule;

   ConfigurationRuleBuilder(final ConfigurationBuilder config, final RuleBuilder rule)
   {
      this.wrapped = config;
      this.rule = rule;
   }

   @Override
   public ConfigurationRuleBuilder addRule(final Rule rule)
   {
      return wrapped.addRule(rule);
   }

   @Override
   public ConfigurationRuleBuilderCustom addRule()
   {
      return wrapped.addRule();
   }

   /**
    * Configure the {@link Parameter} with the given name.
    */
   @Override
   public ConfigurationRuleParameterBuilder where(String name)
   {
      ConfigurationRuleParameterBuilder parameter = new ConfigurationRuleParameterBuilder(this, name);
      rule.getParameterStore().put(name, parameter);
      return parameter;
   }

   /**
    * Set the {@link Condition} of this {@link Rule} instance.
    */
   @Override
   public ConfigurationRuleBuilder when(final Condition condition)
   {
      rule.when(condition);
      return this;
   }

   /**
    * Perform the given {@link Operation} when the conditions set in this {@link Rule} are met.
    */
   @Override
   public ConfigurationRuleBuilder perform(final Operation operation)
   {
      rule.perform(operation);
      return this;
   }

   /**
    * Perform the given {@link Operation} when the conditions set in this {@link Rule} fail to be met.
    */
   @Override
   public ConfigurationRuleBuilder otherwise(final Operation operation)
   {
      wrapped.addRule()
               .when(Not.any(rule))
               .perform(operation);

      return this;
   }

   /**
    * Set the priority of this {@link Rule} instance. If {@link #priority()} differs from the priority of the
    * {@link ConfigurationProvider} from which this rule was returned, then relocate this rule to its new priority
    * position in the compiled rule set.
    */
   @Override
   public ConfigurationRuleBuilder withPriority(int priority)
   {
      rule.withPriority(priority);
      return this;
   }

   /**
    * Set the ID for the current {@link Rule}. This may be used in logging and for rule lookup purposes.
    */
   @Override
   public ConfigurationRuleBuilder withId(String id)
   {
      rule.withId(id);
      return this;
   }

   /**
    * Provides access to the {@link RuleBuilder} for the current {@link Rule}.
    */
   public RuleBuilder getRuleBuilder()
   {
      return rule;
   }

   @Override
   public List<Rule> getRules()
   {
      for (final RuleBuilder rule : wrapped.getRuleBuilders()) {

         ParameterizedCallback callback = new ParameterizedCallback() {
            @Override
            public void call(Parameterized parameterized)
            {
               Set<String> names = parameterized.getRequiredParameterNames();
               ParameterStore store = rule.getParameterStore();

               for (String name : names) {
                  if (!store.contains(name)) {
                     store.put(name, new DefaultParameter(name).bindsTo(Evaluation.property(name)));
                  }
               }

               parameterized.setParameterStore(store);
            }
         };

         Visitor<Condition> conditionVisitor = new ParameterizedConditionVisitor(callback);
         new ConditionVisit(rule).accept(conditionVisitor);

         Visitor<Operation> operationVisitor = new ParameterizedOperationVisitor(callback);
         new OperationVisit(rule).accept(operationVisitor);

      }

      return wrapped.getRules();
   }
}
