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

package utils.nextpage.vat.giant

import config.FrontendAppConfig
import identifiers.WhatIsYourOrganisationId
import play.api.mvc.{Call, Request}
import models.vat.giant.WhatIsYourOrganisation
import controllers.vat.giant.{routes => giantRoutes}
import playconfig.featuretoggle.FeatureConfig
import utils.NextPage

trait WhatIsYourOrganisationNextPage {

  implicit val whatIsYourOrganisation: NextPage[WhatIsYourOrganisationId.type, WhatIsYourOrganisation, Call] = {
    new NextPage[WhatIsYourOrganisationId.type, WhatIsYourOrganisation, Call] {
      override def get(b: WhatIsYourOrganisation)(
        implicit appConfig: FrontendAppConfig,
        featureConfig: FeatureConfig,
        request: Request[_]): Call =
        b match {
          case WhatIsYourOrganisation.Yes => Call("GET", appConfig.emacEnrollmentsUrl(utils.Enrolments.VATGIANT))
          case WhatIsYourOrganisation.No  => giantRoutes.YouDoNotNeedVATController.onPageLoad()
        }
    }
  }
}
