/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers.deenrolment

import javax.inject.Inject

import config.FrontendAppConfig
import controllers.actions._
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import utils.{Enrolments, Enumerable, LoggingHelper, Navigator}
import play.api.mvc.Call
import forms.deenrolment.DoYouNeedToStopMGDFormProvider
import handlers.ErrorHandler
import identifiers.DoYouNeedToStopMGDId
import models.requests.ServiceInfoRequest
import views.html.deenrolment.doYouNeedToStopMGD

class DoYouNeedToStopMGDController @Inject()(
  appConfig: FrontendAppConfig,
  override val messagesApi: MessagesApi,
  navigator: Navigator[Either[String, Call]],
  authenticate: AuthAction,
  serviceInfoData: ServiceInfoAction,
  formProvider: DoYouNeedToStopMGDFormProvider,
  errorHandler: ErrorHandler,
  log: LoggingHelper)
    extends FrontendController
    with I18nSupport
    with Enumerable.Implicits {

  val form = formProvider()

  def onPageLoad() = (authenticate andThen serviceInfoData) { implicit request =>
    Ok(doYouNeedToStopMGD(appConfig, form)(request.serviceInfoContent))
  }

  def onSubmit() = (authenticate andThen serviceInfoData) { implicit request =>
    form
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[_]) =>
          BadRequest(doYouNeedToStopMGD(appConfig, formWithErrors)(request.serviceInfoContent)),
        (value) => {

          val nextPage = navigator.nextPage(DoYouNeedToStopMGDId, (value, mgdEnrolment))

          nextPage match {
            case Right(c) => Redirect(c)
            case Left(s) => {
              log.warn(s"cannot navigate to the next page: $s")
              InternalServerError(errorHandler.internalServerErrorTemplate)
            }
          }
        }
      )
  }

  def mgdEnrolment(implicit r: ServiceInfoRequest[_]) =
    r.request.enrolments.getEnrolment(Enrolments.MachineGamingDuty.toString)
}
