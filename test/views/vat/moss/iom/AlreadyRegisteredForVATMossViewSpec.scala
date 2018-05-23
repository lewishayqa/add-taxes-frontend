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

package views.vat.moss.iom

import play.api.data.Form
import forms.vat.moss.iom.AlreadyRegisteredForVATMossFormProvider
import models.vat.moss.iom.AlreadyRegisteredForVATMoss
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.vat.moss.iom.alreadyRegisteredForVATMoss

class AlreadyRegisteredForVATMossViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "alreadyRegisteredForVATMoss"

  val form = new AlreadyRegisteredForVATMossFormProvider()()

  val serviceInfoContent = HtmlFormat.empty

  def createView = () => alreadyRegisteredForVATMoss(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  def createViewUsingForm =
    (form: Form[_]) => alreadyRegisteredForVATMoss(frontendAppConfig, form)(serviceInfoContent)(fakeRequest, messages)

  "AlreadyRegisteredForVATMoss view" must {
    behave like normalPage(createView, messageKeyPrefix)
  }

  "AlreadyRegisteredForVATMoss view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(form))

        doc.text() must include("You’ll have received a VAT MOSS identification number if you’re already registered")

        for (option <- AlreadyRegisteredForVATMoss.options) {
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
        }
      }
    }

    for (option <- AlreadyRegisteredForVATMoss.options) {
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- AlreadyRegisteredForVATMoss.options.filterNot(o => o == option)) {
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
          }
        }
      }
    }

    "invalid data is sent" must {
      "prepend title with Error: " in {
        val doc = asDocument(createViewUsingForm(form.bind(Map("value" -> ""))))

        assertEqualsMessage(doc, "title", "error.browser.title", messages(s"$messageKeyPrefix.title"))
      }
    }
  }
}
