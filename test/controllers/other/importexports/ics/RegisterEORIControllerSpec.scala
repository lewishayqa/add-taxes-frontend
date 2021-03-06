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

package controllers.other.importexports.ics

import controllers.actions._
import controllers.ControllerSpecBase
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import utils.FakeNavigator
import views.html.other.importexports.ics.registerEORI
import controllers.routes._
import play.api.mvc.Call

class RegisterEORIControllerSpec extends ControllerSpecBase {

  def onwardRoute = IndexController.onPageLoad()

  def controller() =
    new RegisterEORIController(
      frontendAppConfig,
      messagesApi,
      FakeAuthAction,
      FakeServiceInfoAction,
      new FakeNavigator[Call](desiredRoute = onwardRoute))

  def viewAsString() = registerEORI(frontendAppConfig)(HtmlFormat.empty)(fakeRequest, messages).toString

  "RegisterEORI Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

  }
}
