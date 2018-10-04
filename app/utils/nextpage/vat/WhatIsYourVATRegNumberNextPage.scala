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

package utils.nextpage.vat

import config.FrontendAppConfig
import controllers.vat.{routes => vatRoutes}
import identifiers.WhatIsYourVATRegNumberId
import play.api.mvc.{Call, Request}
import uk.gov.hmrc.auth.core.AffinityGroup
import utils.{Enrolments, NextPage}

trait WhatIsYourVATRegNumberNextPage {

  type WhatIsYourVATRegNumberWithRequests = (Boolean, String, Option[AffinityGroup])

  implicit val whatIsYourVATRegNumber
    : NextPage[WhatIsYourVATRegNumberId.type, WhatIsYourVATRegNumberWithRequests, Call] = {
    new NextPage[WhatIsYourVATRegNumberId.type, WhatIsYourVATRegNumberWithRequests, Call] {
      override def get(
        b: WhatIsYourVATRegNumberWithRequests)(implicit appConfig: FrontendAppConfig, request: Request[_]): Call =
        b match {
          case (true, vrn, Some(AffinityGroup.Individual)) => Call("GET", appConfig.vatSignUpClaimSubscriptionUrl(vrn))
          case (false, _, Some(AffinityGroup.Individual))  => Call("GET", appConfig.emacEnrollmentsUrl(Enrolments.VAT))
          case _                                           => vatRoutes.RegisterForVATOnlineController.onPageLoad()
        }
    }
  }
}
