package com.rodaClone.driver.di

import androidx.lifecycle.ViewModel
import com.rodaClone.driver.acceptReject.RespondRequestVM
import com.rodaClone.driver.dialogs.cancel.CancelListVM
import com.rodaClone.driver.dialogs.countrylist.CountryListVM
import com.rodaClone.driver.dialogs.imageCrop.CropImageDialogVm
import com.rodaClone.driver.dialogs.takeMeterPhoto.TakeMeterPhotoVm
import com.rodaClone.driver.dialogs.tripOtp.TripOtpVM
import com.rodaClone.driver.drawer.DrawerVM
import com.rodaClone.driver.drawer.appStatus.AppStatusVm
import com.rodaClone.driver.drawer.approvalDecline.ApprovalVM
import com.rodaClone.driver.drawer.complaints.ComplaintsVM
import com.rodaClone.driver.drawer.dashboard.DashBoardVM
import com.rodaClone.driver.drawer.dashboard.dashBalance.DashBalanceVM
import com.rodaClone.driver.drawer.dashboard.dashFine.DashFineVM
import com.rodaClone.driver.drawer.dashboard.dashHistory.DashHistoryVM
import com.rodaClone.driver.drawer.dashboard.dashRateReward.DashRateRewardVM
import com.rodaClone.driver.drawer.document.documentsList.DocumentsListVM
import com.rodaClone.driver.drawer.document.uploadDocument.UploadDocumentVM
import com.rodaClone.driver.drawer.faq.FaqVM
import com.rodaClone.driver.drawer.history.HistoryVM
import com.rodaClone.driver.drawer.history.cancelledHistory.CancelledHistoryVM
import com.rodaClone.driver.drawer.history.completedHistory.CompletedHistoryListVM
import com.rodaClone.driver.drawer.invoice.InvoiceVM
import com.rodaClone.driver.drawer.language.LanguageVM
import com.rodaClone.driver.drawer.mapfragment.MapFragmentVM
import com.rodaClone.driver.drawer.mapfragment.instantTrip.InstantTripVm
import com.rodaClone.driver.drawer.notification.NotificationVM
import com.rodaClone.driver.drawer.profile.ProfileFragVM
import com.rodaClone.driver.drawer.ratingFragment.RatingVM
import com.rodaClone.driver.drawer.refferal.ReferralVM
import com.rodaClone.driver.drawer.searchPlace.SearchPlaceVm
import com.rodaClone.driver.drawer.sos.SosVM
import com.rodaClone.driver.drawer.subsription.SubscriptionVm
import com.rodaClone.driver.drawer.support.SupportVM
import com.rodaClone.driver.drawer.trip.TripFragmentVM
import com.rodaClone.driver.drawer.tripBill.TripBillVM
import com.rodaClone.driver.drawer.tripCancelled.TripCancelledVM
import com.rodaClone.driver.drawer.vehicleInformation.VehicleInfoVM
import com.rodaClone.driver.drawer.wallet.WalletVm
import com.rodaClone.driver.loginSignup.SignupVM
import com.rodaClone.driver.loginSignup.getStarteedScrn.GetStartedScreenVM
import com.rodaClone.driver.loginSignup.login.LoginVM
import com.rodaClone.driver.loginSignup.otp.OtpVM
import com.rodaClone.driver.loginSignup.signup.SignupFragVM
import com.rodaClone.driver.loginSignup.tour.TourGuideVM
import com.rodaClone.driver.loginSignup.vehicleFrag.VehicleFragVM
import com.rodaClone.driver.splash.SplashVM
import com.rodaClone.driver.transparentAct.TransparentVM
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(SplashVM::class)
    internal abstract fun bindSplashVM(viewModel: SplashVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignupVM::class)
    internal abstract fun bindSSignupVM(viewModel: SignupVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(MapFragmentVM::class)
    internal abstract fun bindMyViewModel(viewModel: MapFragmentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DrawerVM::class)
    internal abstract fun bindDrawerVM(viewModel: DrawerVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GetStartedScreenVM::class)
    internal abstract fun bindGetStartedScreenVM(viewModel: GetStartedScreenVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TourGuideVM::class)
    internal abstract fun bindTourGuideVM(viewModel: TourGuideVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LoginVM::class)
    internal abstract fun bindLoginVM(viewModel: LoginVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CountryListVM::class)
    internal abstract fun bindCountryListVM(viewModel: CountryListVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(OtpVM::class)
    internal abstract fun bindOtpVM(viewModel: OtpVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SignupFragVM::class)
    internal abstract fun bindSignupVMM(viewModel: SignupFragVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VehicleFragVM::class)
    internal abstract fun bindVehicleFragVM(viewModel: VehicleFragVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ProfileFragVM::class)
    internal abstract fun bindProfileFragVM(viewModel: ProfileFragVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(VehicleInfoVM::class)
    internal abstract fun bindVehicleInfoVM(viewModel: VehicleInfoVM): ViewModel



    @Binds
    @IntoMap
    @ViewModelKey(DocumentsListVM::class)
    internal abstract fun bindDocumentsListVM(viewModel: DocumentsListVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UploadDocumentVM::class)
    internal abstract fun bindUploadDocumentVM(viewModel: UploadDocumentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SupportVM::class)
    internal abstract fun bindSupportVM(viewModel: SupportVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ComplaintsVM::class)
    internal abstract fun bindComplaintsVM(viewModel: ComplaintsVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FaqVM::class)
    internal abstract fun bindFaqVM(viewModel: FaqVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SosVM::class)
    internal abstract fun bindSosVM(viewModel: SosVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashBoardVM::class)
    internal abstract fun bindDashBoardVM(viewModel: DashBoardVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HistoryVM::class)
    internal abstract fun bindHistoryVM(viewModel: HistoryVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ReferralVM::class)
    internal abstract fun bindReferralVM(viewModel: ReferralVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(NotificationVM::class)
    internal abstract fun bindNotificationVM(viewModel: NotificationVM): ViewModel


    @Binds
    @IntoMap
    @ViewModelKey(TripFragmentVM::class)
    internal abstract fun bindTripFragmentVM(viewModel: TripFragmentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CancelListVM::class)
    internal abstract fun bindCancelListVM(viewModel: CancelListVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InvoiceVM::class)
    internal abstract fun bindInvoiceVM(viewModel: InvoiceVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RatingVM::class)
    internal abstract fun bindRatingVM(viewModel: RatingVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TripCancelledVM::class)
    internal abstract fun bindTripCancelledVM(viewModel: TripCancelledVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ApprovalVM::class)
    internal abstract fun bindApprovalVM(viewModel: ApprovalVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(WalletVm::class)
    internal abstract fun bindWalletVM(viewModel: WalletVm): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashBalanceVM::class)
    internal abstract fun bindDashBalanceVM(viewModel: DashBalanceVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashFineVM::class)
    internal abstract fun bindDashDashFineVM(viewModel: DashFineVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashHistoryVM::class)
    internal abstract fun bindDashHistoryVM(viewModel: DashHistoryVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(DashRateRewardVM::class)
    internal abstract fun bindDasDashRateRewardVM(viewModel: DashRateRewardVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TripOtpVM::class)
    internal abstract fun bindTripOtpVM(viewModel: TripOtpVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SubscriptionVm::class)
    internal abstract fun bindSubscriptionvm(viewModel: SubscriptionVm): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CompletedHistoryListVM::class)
    internal abstract fun bindChildHistoryListVM(viewModel: CompletedHistoryListVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CancelledHistoryVM::class)
    internal abstract fun bindCancelledHistoryVM(viewModel: CancelledHistoryVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(InstantTripVm::class)
    internal abstract fun bindInstantTripVm(viewModel: InstantTripVm): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchPlaceVm::class)
    internal abstract fun bindSearchPlaceVm(viewModel: SearchPlaceVm): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TripBillVM::class)
    internal abstract fun bindTripBillVM(viewModel: TripBillVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TakeMeterPhotoVm::class)
    internal abstract fun bindTakeMeterPhotoVm(viewModel: TakeMeterPhotoVm): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AppStatusVm::class)
    internal abstract fun bindAppStatusVm(viewModel: AppStatusVm): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LanguageVM::class)
    internal abstract fun bindLanguageVM(viewModel: LanguageVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(CropImageDialogVm::class)
    internal abstract fun bindCropImageVm(viewModel: CropImageDialogVm): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TransparentVM::class)
    internal abstract fun bindTransparentVM(viewModel: TransparentVM): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RespondRequestVM::class)
    internal abstract fun bindRespondRequestVM(viewModel: RespondRequestVM): ViewModel

}