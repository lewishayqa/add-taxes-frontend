/*
 * Copyright 2018 HM Revenue & Customs
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

package views.vat

import play.api.data.Form
import forms.vat.RegisterForVATOnlineFormProvider
import models.vat.RegisterForVATOnline
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.registerForVATOnline

class RegisterForVATOnlineViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerForVATOnline"

  val form = new RegisterForVATOnlineFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => registerForVATOnline(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) => registerForVATOnline(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def viewIncludes(s: String): Unit = asDocument(createView()).text() must include(s)

  "RegisterForVATOnline view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "RegisterForVATOnline view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))
        for (option <- RegisterForVATOnline.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }

      "include header" in {
        viewIncludes("Do you want to register for VAT online?")
      }

      "include list items" in {
        viewIncludes("need to apply for a ‘registration exception’")
        viewIncludes("are joining the Agricultural Flat Rate Scheme")
        viewIncludes("are registering the divisions or business units of a body corporate under separate VAT numbers")
        viewIncludes("have an EU business ‘distance selling’ to the UK")
        viewIncludes("have imported (‘acquired’) goods worth more than £85,000 from another EU company")
        viewIncludes("are disposing of assets on which 8th or 13th Directive refunds have been claimed")
      }
    }

    for (option <- RegisterForVATOnline.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- RegisterForVATOnline.options.filterNot(_ == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> ""))))
        val title = messages("site.service_title", messages(s"$messageKeyPrefix.title"))

        assertEqualsMessage(doc, "title", "error.browser.title", title)
      }
    }
  }
}
