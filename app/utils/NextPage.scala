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

package utils

import controllers.other.oil.routes
import controllers.other.importexports.dan.{routes => danRoutes}
import controllers.other.importexports.ebti.{routes => ebtiRoutes}
import controllers.other.importexports.emcs.{routes => emcsRoutes}
import controllers.other.gambling.gbd.{routes => gbdRoutes}
import controllers.other.importexports.ics.{routes => icsRoutes}
import controllers.other.importexports.ncts.{routes => nctsRoutes}
import controllers.other.importexports.nes.{routes => nesRoutes}
import identifiers._
import models.other.importexports.{DoYouHaveEORINumber, DoYouWantToAddImportExport}
import models.other.importexports.emcs.DoYouHaveASEEDNumber
import models.OtherTaxes
import models.other.gambling.gbd.AreYouRegisteredGTS
import models.other.importexports.dan.DoYouHaveDAN
import models.other.importexports.nes.DoYouHaveCHIEFRole
import models.other.oil.SelectAnOilService.{RebatedOilsEnquiryService, TiedOilsEnquiryService}
import models.other.oil.{HaveYouRegisteredForRebatedOils, HaveYouRegisteredForTiedOils, SelectAnOilService}
import models.wrongcredentials.FindingYourAccount
import play.api.mvc.Call
import play.api.mvc.Request

trait NextPage[A, B] {
  def get(b: B)(implicit urlHelper: UrlHelper, request: Request[_]): Call
}

object NextPage {

  implicit val whichPensionSchemeToAdd: NextPage[WhichPensionSchemeToAddId.type,
    models.employer.pension.WhichPensionSchemeToAdd] = {
    new NextPage[WhichPensionSchemeToAddId.type, models.employer.pension.WhichPensionSchemeToAdd] {
      override def get(b: models.employer.pension.WhichPensionSchemeToAdd)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case models.employer.pension.WhichPensionSchemeToAdd.Administrators => Call("GET", urlHelper.getPortalURL("pensionAdministrators"))
          case models.employer.pension.WhichPensionSchemeToAdd.Practitioners => Call("GET", urlHelper.getPortalURL("pensionPractitioners"))
        }
    }
  }

  implicit val areYouRegisteredGTS: NextPage[AreYouRegisteredGTSId.type, AreYouRegisteredGTS] = {
    new NextPage[AreYouRegisteredGTSId.type, AreYouRegisteredGTS] {
      override def get(b: AreYouRegisteredGTS)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case AreYouRegisteredGTS.Yes => Call(GET, urlHelper.emacEnrollmentsUrl(Enrolments.GeneralBetting))
          case AreYouRegisteredGTS.No => gbdRoutes.RegisterGBDController.onPageLoad()
        }
     }
  }

  implicit val doYouWantToAddImportExport: NextPage[DoYouWantToAddImportExportId.type, DoYouWantToAddImportExport] = {
    new NextPage[DoYouWantToAddImportExportId.type, models.other.importexports.DoYouWantToAddImportExport] {
      override def get(b: models.other.importexports.DoYouWantToAddImportExport)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case DoYouWantToAddImportExport.EMCS => emcsRoutes.DoYouHaveASEEDNumberController.onPageLoad()
          case DoYouWantToAddImportExport.ICS => icsRoutes.DoYouHaveEORINumberController.onPageLoad()
          case DoYouWantToAddImportExport.DDES => danRoutes.DoYouHaveDANController.onPageLoad()
          case DoYouWantToAddImportExport.NOVA => Call("GET", urlHelper.getPortalURL("novaEnrolment"))
          case DoYouWantToAddImportExport.NCTS => nctsRoutes.DoYouHaveEORINumberController.onPageLoad()
          case DoYouWantToAddImportExport.eBTI => ebtiRoutes.DoYouHaveEORINumberController.onPageLoad()
          case DoYouWantToAddImportExport.NES => nesRoutes.DoYouHaveEORINumberController.onPageLoad()
          case DoYouWantToAddImportExport.ISD => Call("GET", urlHelper.getHmceURL("isd"))
        }
    }
  }


  implicit val doYouHaveCHIEFHasEORIRole: NextPage[DoYouHaveCHIEFRoleId.HasEORI.type,
    DoYouHaveCHIEFRole] = {
    new NextPage[DoYouHaveCHIEFRoleId.HasEORI.type, DoYouHaveCHIEFRole] {
      override def get(b: DoYouHaveCHIEFRole)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case DoYouHaveCHIEFRole.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.NewExportSystem))
          case DoYouHaveCHIEFRole.No => nesRoutes.GetCHIEFRoleController.onPageLoad()
        }
     }
  }

  implicit val doYouHaveCHIEFNoEORIRole: NextPage[DoYouHaveCHIEFRoleId.NoEORI.type,
    DoYouHaveCHIEFRole] = {
    new NextPage[DoYouHaveCHIEFRoleId.NoEORI.type, DoYouHaveCHIEFRole] {
      override def get(b: DoYouHaveCHIEFRole)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case DoYouHaveCHIEFRole.Yes =>  nesRoutes.RegisterEORIController.onPageLoad()
          case DoYouHaveCHIEFRole.No => nesRoutes.GetEoriAndChiefRoleController.onPageLoad()
        }
    }
  }

  implicit val doYouHaveDAN: NextPage[DoYouHaveDANId.type,
    DoYouHaveDAN] = {
    new NextPage[DoYouHaveDANId.type, DoYouHaveDAN] {
      override def get(b: DoYouHaveDAN)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case DoYouHaveDAN.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.DefermentApprovalNumber))
          case DoYouHaveDAN.No => danRoutes.RegisterDefermentApprovalNumberController.onPageLoad()
        }
     }
  }

  implicit val doYouHaveASEEDNumber: NextPage[DoYouHaveASEEDNumberId.type,
    DoYouHaveASEEDNumber] = {
    new NextPage[DoYouHaveASEEDNumberId.type, DoYouHaveASEEDNumber] {
      override def get(b: DoYouHaveASEEDNumber)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case DoYouHaveASEEDNumber.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.ExciseMovementControlSystem))
          case DoYouHaveASEEDNumber.No => emcsRoutes.RegisterExciseMovementControlSystemController.onPageLoad()
        }
     }
  }

  implicit val icsEori: NextPage[DoYouHaveEORINumberId.ICS.type,
    DoYouHaveEORINumber] = {
    new NextPage[DoYouHaveEORINumberId.ICS.type, DoYouHaveEORINumber] {
      override def get(b: DoYouHaveEORINumber)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case DoYouHaveEORINumber.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.EconomicOperatorsRegistration))
          case DoYouHaveEORINumber.No => icsRoutes.RegisterEORIController.onPageLoad()
        }
     }
  };

  implicit val ebtiEori: NextPage[DoYouHaveEORINumberId.EBTI.type,
    DoYouHaveEORINumber] = {
    new NextPage[DoYouHaveEORINumberId.EBTI.type, DoYouHaveEORINumber] {
      override def get(b: DoYouHaveEORINumber)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case DoYouHaveEORINumber.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.ElectronicBindingTariffInformation))
          case DoYouHaveEORINumber.No => ebtiRoutes.RegisterEORIController.onPageLoad()
        }
    }
  }

  implicit val nctsEori: NextPage[DoYouHaveEORINumberId.NCTS.type,
    DoYouHaveEORINumber] = {
    new NextPage[DoYouHaveEORINumberId.NCTS.type, DoYouHaveEORINumber] {
      override def get(b: DoYouHaveEORINumber)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case DoYouHaveEORINumber.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.NewComputerisedTransitSystem))
          case DoYouHaveEORINumber.No => nctsRoutes.RegisterEORIController.onPageLoad()
        }
    }
  }

  implicit val nesEori: NextPage[DoYouHaveEORINumberId.NES.type,
    DoYouHaveEORINumber] = {
    new NextPage[DoYouHaveEORINumberId.NES.type, DoYouHaveEORINumber] {
      override def get(b: DoYouHaveEORINumber)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case DoYouHaveEORINumber.Yes => nesRoutes.DoYouHaveCHIEFRoleHasEORIController.onPageLoad()
          case DoYouHaveEORINumber.No => nesRoutes.DoYouHaveCHIEFRoleNoEORIController.onPageLoad()
        }
    }
  }

  implicit val otherTaxes: NextPage[OtherTaxesId.type,
    OtherTaxes] = {
    new NextPage[OtherTaxesId.type, OtherTaxes] {
      override def get(b: OtherTaxes)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case models.OtherTaxes.AlcoholAndTobacco => Call("GET", urlHelper.businessTaxAccountLink("alcohol"))
          case models.OtherTaxes.AutomaticExchangeOfInformation => Call("GET", urlHelper.businessTaxAccountLink("aeoi"))
          case models.OtherTaxes.Charities => Call("GET", urlHelper.businessTaxAccountLink("charities"))
          case models.OtherTaxes.GamblingAndGaming => Call("GET", urlHelper.businessTaxAccountLink("gambling"))
          case models.OtherTaxes.HousingAndLand => Call("GET", urlHelper.businessTaxAccountLink("land"))
          case models.OtherTaxes.ImportsExports => Call("GET", urlHelper.businessTaxAccountLink("import-export"))
          case models.OtherTaxes.OilAndFuel => routes.SelectAnOilServiceController.onPageLoad()
          case models.OtherTaxes.FulfilmentHouseDueDiligenceSchemeIntegration => Call("GET", urlHelper.fulfilmentHouse())
        }
     }
  }

  implicit val findingYourAccount: NextPage[FindingYourAccountId.type,
    FindingYourAccount] = {
    new NextPage[FindingYourAccountId.type, FindingYourAccount] {
      override def get(b: FindingYourAccount)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case FindingYourAccount.DontKnowPassword => Call("GET", urlHelper.governmentGatewayLostCredentialsUrl(ForgottenOptions.ForgottenPassword))
          case FindingYourAccount.DontKnowId => Call("GET", urlHelper.governmentGatewayLostCredentialsUrl(ForgottenOptions.ForgottenId))
          case FindingYourAccount.DontKnowIdOrPassword => Call("GET", urlHelper.governmentGatewayLostCredentialsUrl(ForgottenOptions.ForgottenIdAndPassword))
        }
     }
  }

  implicit val selectAnOilService: NextPage[SelectAnOilServiceId.type, SelectAnOilService] =
    new NextPage[SelectAnOilServiceId.type, SelectAnOilService] {
      override def get(b: SelectAnOilService)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case RebatedOilsEnquiryService => routes.HaveYouRegisteredForRebatedOilsController.onPageLoad()
          case TiedOilsEnquiryService => routes.HaveYouRegisteredForTiedOilsController.onPageLoad()
        }
    }

  implicit val haveYouRegisteredForTiedOils: NextPage[HaveYouRegisteredForTiedOilsId.type,
    HaveYouRegisteredForTiedOils] = {
    new NextPage[HaveYouRegisteredForTiedOilsId.type, HaveYouRegisteredForTiedOils] {
      override def get(b: HaveYouRegisteredForTiedOils)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case HaveYouRegisteredForTiedOils.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.TiedOils))
          case HaveYouRegisteredForTiedOils.No => routes.RegisterTiedOilsController.onPageLoad()
        }
    }
  }

  implicit val haveYouRegisteredForRebatedOils: NextPage[HaveYouRegisteredForRebatedOilsId.type,
    HaveYouRegisteredForRebatedOils] = {
    new NextPage[HaveYouRegisteredForRebatedOilsId.type, HaveYouRegisteredForRebatedOils] {
      override def get(b: HaveYouRegisteredForRebatedOils)(implicit urlHelper: UrlHelper, request: Request[_]): Call =
        b match {
          case HaveYouRegisteredForRebatedOils.Yes => Call("GET", urlHelper.emacEnrollmentsUrl(Enrolments.RebatedOils))
          case HaveYouRegisteredForRebatedOils.No => routes.RegisterRebatedOilsController.onPageLoad()
        }
    }
  }

  private val GET: String = "GET"
}
