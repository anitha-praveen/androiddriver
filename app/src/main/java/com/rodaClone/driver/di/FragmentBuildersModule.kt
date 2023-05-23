package com.rodaClone.driver.di

import com.rodaClone.driver.acceptReject.RespondRequest
import com.rodaClone.driver.dialogs.cancel.CancelDialog
import com.rodaClone.driver.dialogs.countrylist.CountryListDialog
import com.rodaClone.driver.dialogs.imageCrop.CropImageDialog
import com.rodaClone.driver.dialogs.takeMeterPhoto.TakeMeterPhotoDialog
import com.rodaClone.driver.dialogs.tripOtp.TripOtpDialog
import com.rodaClone.driver.drawer.appStatus.AppStatusFrag
import com.rodaClone.driver.drawer.approvalDecline.ApprovalFragment
import com.rodaClone.driver.drawer.complaints.ComplaintsFragment
import com.rodaClone.driver.drawer.dashboard.DashBoardFragment
import com.rodaClone.driver.drawer.dashboard.dashBalance.DashBalanceFragment
import com.rodaClone.driver.drawer.dashboard.dashFine.DashFineFragment
import com.rodaClone.driver.drawer.dashboard.dashHistory.DashHistoryFragment
import com.rodaClone.driver.drawer.dashboard.dashRateReward.DashRateRewardFragment
import com.rodaClone.driver.drawer.document.documentsList.DocumentsListFragment
import com.rodaClone.driver.drawer.document.uploadDocument.UploadDocument
import com.rodaClone.driver.drawer.faq.FaqFragment
import com.rodaClone.driver.drawer.history.HistoryFragment
import com.rodaClone.driver.drawer.history.cancelledHistory.CancelledHistoryFragment
import com.rodaClone.driver.drawer.history.completedHistory.CompletedHistoryList
import com.rodaClone.driver.drawer.invoice.InvoiceFragment
import com.rodaClone.driver.drawer.language.LanguageFrag
import com.rodaClone.driver.drawer.mapfragment.MapFragment
import com.rodaClone.driver.drawer.mapfragment.instantTrip.InstantTripFrag
import com.rodaClone.driver.drawer.notification.NotificationFragment
import com.rodaClone.driver.drawer.profile.ProfileFragment
import com.rodaClone.driver.drawer.ratingFragment.RatingFragment
import com.rodaClone.driver.drawer.refferal.ReferralFragment
import com.rodaClone.driver.drawer.searchPlace.FragSearchPlace
import com.rodaClone.driver.drawer.sos.SosFragment
import com.rodaClone.driver.drawer.subsription.SubscriptionFragment
import com.rodaClone.driver.drawer.support.SupportFragment
import com.rodaClone.driver.drawer.trip.TripFragment
import com.rodaClone.driver.drawer.tripBill.TripBillFrag
import com.rodaClone.driver.drawer.tripCancelled.TripCancelledFragment
import com.rodaClone.driver.drawer.vehicleInformation.VehicleInfoFragment
import com.rodaClone.driver.drawer.wallet.WalletFragment
import com.rodaClone.driver.loginSignup.getStarteedScrn.GetStartedScreen
import com.rodaClone.driver.loginSignup.login.LoginFragment
import com.rodaClone.driver.loginSignup.otp.OtpFragment
import com.rodaClone.driver.loginSignup.signup.SignupFragFragment
import com.rodaClone.driver.loginSignup.tour.TourGuideFrag
import com.rodaClone.driver.loginSignup.vehicleFrag.VehicleFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributesMapFragment(): MapFragment

//    @ContributesAndroidInjector
//    abstract fun contributesSecondFragment(): SecondFragment

    @ContributesAndroidInjector
    abstract fun contributeGetStartedFrag(): GetStartedScreen

    @ContributesAndroidInjector
    abstract fun contributeTourGuideFrag(): TourGuideFrag

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeCountryListDialog(): CountryListDialog

    @ContributesAndroidInjector
    abstract fun contributeOtpFrag(): OtpFragment

    @ContributesAndroidInjector
    abstract fun contributeSignupFrag(): SignupFragFragment

    @ContributesAndroidInjector
    abstract fun contributeVehicleFragment(): VehicleFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeVehicleInfoFragment(): VehicleInfoFragment



    @ContributesAndroidInjector
    abstract fun contributeDocumentListFragment(): DocumentsListFragment

    @ContributesAndroidInjector
    abstract fun contributeUploadDocumentDialog(): UploadDocument

    @ContributesAndroidInjector
    abstract fun contributeSupportFragment(): SupportFragment

    @ContributesAndroidInjector
    abstract fun contributeComplaintsFragment(): ComplaintsFragment

    @ContributesAndroidInjector
    abstract fun contributeFaqFragment(): FaqFragment

    @ContributesAndroidInjector
    abstract fun contributeSosFragment(): SosFragment

    @ContributesAndroidInjector
    abstract fun contributeDashBoardFragment(): DashBoardFragment

    @ContributesAndroidInjector
    abstract fun contributeHistoryFragment(): HistoryFragment

    @ContributesAndroidInjector
    abstract fun contributeReferralFragment(): ReferralFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationFragment(): NotificationFragment

    @ContributesAndroidInjector
    abstract fun contributeTripFragment(): TripFragment

    @ContributesAndroidInjector
    abstract fun contributeCancelDialog(): CancelDialog

    @ContributesAndroidInjector
    abstract fun contributeInvoiceFragment(): InvoiceFragment

    @ContributesAndroidInjector
    abstract fun contributeApprovalFragment(): ApprovalFragment

    @ContributesAndroidInjector
    abstract fun contributeRatingFragment(): RatingFragment

    @ContributesAndroidInjector
    abstract fun contributeTripCancelledDialog(): TripCancelledFragment

    @ContributesAndroidInjector
    abstract fun contributeWalletFragment(): WalletFragment

    @ContributesAndroidInjector
    abstract fun contributeDashHistoryFragment(): DashHistoryFragment

    @ContributesAndroidInjector
    abstract fun contributeDashBalanceFragment(): DashBalanceFragment

    @ContributesAndroidInjector
    abstract fun contributeDashFineFragment(): DashFineFragment

    @ContributesAndroidInjector
    abstract fun DashRateRewardFragment(): DashRateRewardFragment

    @ContributesAndroidInjector
    abstract fun contributeTripOtpDialog(): TripOtpDialog

    @ContributesAndroidInjector
    abstract fun contributeSubscription(): SubscriptionFragment

    @ContributesAndroidInjector
    abstract fun contributeChildHistoryList(): CompletedHistoryList

    @ContributesAndroidInjector
    abstract fun contributeCancelledHistoryFragment(): CancelledHistoryFragment

    @ContributesAndroidInjector
    abstract fun contributeInstantTrip(): InstantTripFrag

    @ContributesAndroidInjector
    abstract fun contributeSearchPlace(): FragSearchPlace

    @ContributesAndroidInjector
    abstract fun contributeTripBillFrag(): TripBillFrag

    @ContributesAndroidInjector
    abstract fun contributeTakeMeterPhoto(): TakeMeterPhotoDialog

    @ContributesAndroidInjector
    abstract fun contributeAppStatus(): AppStatusFrag

    @ContributesAndroidInjector
    abstract fun contributeLanguageFrag(): LanguageFrag

    @ContributesAndroidInjector
    abstract fun contributeCropImage(): CropImageDialog

    @ContributesAndroidInjector
    abstract fun contributeRespondRequest(): RespondRequest
}