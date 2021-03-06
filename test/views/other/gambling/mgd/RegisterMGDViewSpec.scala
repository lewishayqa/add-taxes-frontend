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

package views.other.gambling.mgd

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.other.gambling.mgd.registerMGD

class RegisterMGDViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "registerMGD"

  def createView = () => registerMGD(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages)

  "RegisterMGD view" must {
    behave like normalPage(createView, messageKeyPrefix)

    "Render the correct content" in {
      val doc = asDocument(createView())
      val view = doc.text()

      assertLinkById(
        doc,
        "continue",
        "Register for Machine Games Duty",
        "http://localhost:8080/portal/business-registration/mgd/type-of-business?lang=eng",
        "RegisterMgd:Click:Continue"
      )

      assertContainsText(
        doc,
        "When you get your registration number, sign in to your account and add Machine Games Duty.")
    }
  }
}
