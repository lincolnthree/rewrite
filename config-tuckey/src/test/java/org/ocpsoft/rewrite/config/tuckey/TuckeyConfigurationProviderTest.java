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
package org.ocpsoft.rewrite.config.tuckey;

import junit.framework.Assert;

import org.apache.http.client.methods.HttpGet;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.ocpsoft.rewrite.test.HttpAction;
import org.ocpsoft.rewrite.test.RewriteTest;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@RunWith(Arquillian.class)
public class TuckeyConfigurationProviderTest extends RewriteTest
{
   @Deployment(testable = true)
   public static WebArchive getDeployment()
   {
      WebArchive deployment = RewriteTest.getDeployment()
               .addAsLibraries(resolveDependencies("org.tuckey:urlrewritefilter:3.1.0"))
               .addAsWebInfResource("urlrewrite.xml")
               .addPackages(true, TuckeyRoot.class.getPackage());

      return deployment;
   }

   @Test
   public void testConfigurationIntegratesWithRedirectFlow()
   {
      HttpAction<HttpGet> action = get("/some/olddir/value");
      Assert.assertEquals("/very/newdir/value", action.getCurrentURL());
      Assert.assertEquals(404, action.getStatusCode());
   }

   @Test
   public void testConfigurationIntegratesWithForwardFlow()
   {
      HttpAction<HttpGet> action = get("/some/fordir/value");
      Assert.assertEquals("/very/newdir/value", action.getCurrentURL());
      Assert.assertEquals(404, action.getStatusCode());
   }

   @Test
   public void testConfigurationIntegratesWithForwardFlowNonRedirecting()
   {
      HttpAction<HttpGet> action = get("/some/fordir/nonredirect");
      Assert.assertEquals("/some/fordir/nonredirect", action.getCurrentContextRelativeURL());
      Assert.assertEquals(200, action.getStatusCode());
   }

   @Test
   public void testConfigurationIntegratesWithForwardFlowNonRedirecting404()
   {
      HttpAction<HttpGet> action = get("/some/404/dir");
      Assert.assertEquals("/some/404/dir", action.getCurrentContextRelativeURL());
      Assert.assertEquals(404, action.getStatusCode());
   }

}
