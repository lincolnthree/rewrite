/*
 * Copyright 2011 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
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
package org.ocpsoft.rewrite.param;

import java.util.ArrayList;
import java.util.List;

import org.ocpsoft.rewrite.bind.Binding;
import org.ocpsoft.rewrite.bind.Convertable;
import org.ocpsoft.rewrite.bind.Converter;
import org.ocpsoft.rewrite.bind.DefaultBindable;
import org.ocpsoft.rewrite.bind.Validatable;
import org.ocpsoft.rewrite.bind.Validator;
import org.ocpsoft.rewrite.context.EvaluationContext;
import org.ocpsoft.rewrite.event.Rewrite;
import org.ocpsoft.rewrite.util.ValueHolderUtil;

/**
 * An base implementation of {@link Parameter}
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class ParameterBuilder<IMPLTYPE extends ParameterBuilder<IMPLTYPE>> extends DefaultBindable<IMPLTYPE>
         implements Parameter<IMPLTYPE>
{
   private final List<Transform<String>> transforms = new ArrayList<Transform<String>>();
   private final List<Constraint<String>> constraints = new ArrayList<Constraint<String>>();
   private Converter<?> converter = null;
   private Validator<?> validator = null;
   private String name;

   /**
    * Create a new {@link ParameterBuilder} instance with the given name.
    */
   protected ParameterBuilder(String name)
   {
      this.name = name;
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public Object convert(Rewrite event, EvaluationContext context, Object value)
   {
      return ValueHolderUtil.convert(event, context, converter, value);
   }

   @Override
   public boolean validate(Rewrite event, EvaluationContext context, Object value)
   {
      return ValueHolderUtil.validates(event, context, validator, value);
   }

   @Override
   public IMPLTYPE bindsTo(Binding binding)
   {
      if (binding instanceof Convertable && converter != null)
      {
         ((Convertable<?>) binding).convertedBy(converter);
      }
      if (binding instanceof Validatable && validator != null)
      {
         ((Validatable<?>) binding).validatedBy(validator);
      }

      return super.bindsTo(binding);
   }

   @Override
   @SuppressWarnings("unchecked")
   public <X extends Converter<?>> IMPLTYPE convertedBy(Class<X> type)
   {
      this.converter = ValueHolderUtil.resolveConverter(type);
      for (Binding binding : getBindings()) {
         if (binding instanceof Convertable)
         {
            ((Convertable<IMPLTYPE>) binding).convertedBy(converter);
         }
      }

      return (IMPLTYPE) this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public IMPLTYPE convertedBy(Converter<?> converter)
   {
      this.converter = converter;
      for (Binding binding : getBindings()) {
         if (binding instanceof Convertable)
         {
            ((Convertable<IMPLTYPE>) binding).convertedBy(converter);
         }
      }

      return (IMPLTYPE) this;
   }

   @Override
   public Converter<?> getConverter()
   {
      return converter;
   }

   @Override
   @SuppressWarnings("unchecked")
   public <X extends Validator<?>> IMPLTYPE validatedBy(Class<X> type)
   {
      this.validator = ValueHolderUtil.resolveValidator(type);
      for (Binding binding : getBindings()) {
         if (binding instanceof Validatable)
         {
            ((Validatable<IMPLTYPE>) binding).validatedBy(validator);
         }
      }

      return (IMPLTYPE) this;
   }

   @Override
   @SuppressWarnings("unchecked")
   public IMPLTYPE validatedBy(Validator<?> validator)
   {
      this.validator = validator;
      for (Binding binding : getBindings()) {
         if (binding instanceof Validatable)
         {
            ((Validatable<IMPLTYPE>) binding).validatedBy(validator);
         }
      }

      return (IMPLTYPE) this;
   }

   @Override
   public Validator<?> getValidator()
   {
      return validator;
   }

   @Override
   @SuppressWarnings("unchecked")
   public IMPLTYPE constrainedBy(Constraint<String> constraint)
   {
      this.constraints.add(constraint);

      return (IMPLTYPE) this;
   }

   @Override
   public List<Constraint<String>> getConstraints()
   {
      return constraints;
   }

   @Override
   @SuppressWarnings("unchecked")
   public IMPLTYPE transformedBy(Transform<String> transform)
   {
      this.transforms.add(transform);

      return (IMPLTYPE) this;
   }

   @Override
   public List<Transform<String>> getTransforms()
   {
      return transforms;
   }

   @Override
   public String toString()
   {
      return "ParameterBuilder [" + name + " -> transforms=" + transforms + ", constraints=" + constraints
               + ", bindings=" + getBindings() + ", converter=" + converter + ", validator=" + validator + "]";
   }

}
