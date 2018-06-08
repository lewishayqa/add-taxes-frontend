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
import controllers.vat.ec.{routes => ecRoutes}
import controllers.vat.eurefunds.{routes => euRoutes}
import controllers.vat.moss.{routes => mossRoutes}
import controllers.vat.rcsl.{routes => rcslRoutes}
import identifiers.WhichVATServicesToAddId
import models.vat.WhichVATServicesToAdd
import play.api.mvc.{Call, Request}
import uk.gov.hmrc.auth.core.AffinityGroup
import uk.gov.hmrc.auth.core.Enrolments
import utils.{HmrcEnrolmentType, NextPage}

trait WhichVATServicesToAddNextPage {

  type WhichVATServicesToAddWithAffinityWithEnrolments = (WhichVATServicesToAdd, Option[AffinityGroup], Enrolments)


  implicit val whichVATServicesToAdd
    : NextPage[WhichVATServicesToAddId.type, WhichVATServicesToAddWithAffinityWithEnrolments] = {
    new NextPage[WhichVATServicesToAddId.type, WhichVATServicesToAddWithAffinityWithEnrolments] {
      override def get(b: WhichVATServicesToAddWithAffinityWithEnrolments)(
        implicit appConfig: FrontendAppConfig,
        request: Request[_]): Call = {

        val (serviceToAdd, _, _) = b

        serviceToAdd match {
          case WhichVATServicesToAdd.VAT       => Call("GET", appConfig.getPortalUrl("businessRegistration"))
          case WhichVATServicesToAdd.ECSales   => ecRoutes.RegisteredForVATECSalesController.onPageLoad()
          case WhichVATServicesToAdd.EURefunds => euRoutes.RegisteredForVATEURefundsController.onPageLoad()
          case WhichVATServicesToAdd.RCSL      => rcslRoutes.RegisteredForVATRCSLController.onPageLoad()
          case WhichVATServicesToAdd.MOSS      => getVATMOSSCall(b)
          case WhichVATServicesToAdd.NOVA      => Call("GET", appConfig.getPortalUrl("novaEnrolment"))
        }
      }
    }
  }

  def getVATMOSSCall(b: WhichVATServicesToAddWithAffinityWithEnrolments): Call = {
    val (_, affinity, enrolments) = b
    val hasVAT: Boolean = utils.Enrolments.hasEnrolments(enrolments, HmrcEnrolmentType.VAT)

    (affinity, hasVAT) match {
      case (Some(AffinityGroup.Individual), _) => ???
      case (_, true)                           => ???
      case (_, false)                          => mossRoutes.WhereIsYourBusinessBasedController.onPageLoad()
    }
  }
}
