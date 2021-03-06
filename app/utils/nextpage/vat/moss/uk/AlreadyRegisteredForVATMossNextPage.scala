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

package utils.nextpage.vat.moss.uk

import config.FrontendAppConfig
import identifiers.AlreadyRegisteredForVATMossId
import models.vat.moss.AlreadyRegisteredForVATMoss
import play.api.mvc.{Call, Request}
import utils.{Enrolments, NextPage}

trait AlreadyRegisteredForVATMossNextPage {

  implicit val alreadyRegisteredForVATMossUk
    : NextPage[AlreadyRegisteredForVATMossId.UkBased.type, AlreadyRegisteredForVATMoss, Call] = {
    new NextPage[AlreadyRegisteredForVATMossId.UkBased.type, AlreadyRegisteredForVATMoss, Call] {
      override def get(
        b: AlreadyRegisteredForVATMoss)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case AlreadyRegisteredForVATMoss.Yes => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.VATMOSS))
          case AlreadyRegisteredForVATMoss.No  => Call("GET", appConfig.getPortalUrl("mossRegistration"))
        }
    }
  }
}
