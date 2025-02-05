package org.oppia.android.app.player.audio

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.oppia.android.app.fragment.FragmentComponentImpl
import org.oppia.android.app.fragment.InjectableFragment
import org.oppia.android.app.model.ProfileId
import org.oppia.android.app.model.State
import org.oppia.android.util.profile.CurrentUserProfileIdIntentDecorator.decorateWithUserProfileId
import org.oppia.android.util.profile.CurrentUserProfileIdIntentDecorator.extractCurrentUserProfileId
import javax.inject.Inject

/** Fragment that controls audio for a content-card. */
class AudioFragment :
  InjectableFragment(),
  LanguageInterface,
  AudioUiManager,
  CellularDataInterface {
  @Inject
  lateinit var audioFragmentPresenter: AudioFragmentPresenter

  companion object {
    /**
     * Creates a new instance of a AudioFragment.
     * @param profileId used by AudioFragment to get Audio Language.
     * @return a new instance of [AudioFragment].
     */
    fun newInstance(internalProfileId: Int): AudioFragment {

      val profileId = ProfileId.newBuilder().setInternalId(internalProfileId).build()
      val audioFragment = AudioFragment()
      val args = Bundle()
      args.decorateWithUserProfileId(profileId)
      audioFragment.arguments = args
      return audioFragment
    }
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)
    (fragmentComponent as FragmentComponentImpl).inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    super.onCreateView(inflater, container, savedInstanceState)
    val internalProfileId =
      arguments?.extractCurrentUserProfileId()?.internalId ?: -1
    return audioFragmentPresenter.handleCreateView(inflater, container, internalProfileId)
  }

  override fun languageSelectionClicked() {
    audioFragmentPresenter.showLanguageDialogFragment()
  }

  override fun onLanguageSelected(currentLanguageCode: String) {
    audioFragmentPresenter.languageSelected(currentLanguageCode)
  }

  override fun onStop() {
    super.onStop()
    audioFragmentPresenter.handleOnStop()
  }

  override fun onDestroy() {
    super.onDestroy()
    audioFragmentPresenter.handleOnDestroy()
  }

  override fun enableAudioPlayback(contentId: String?) {
    audioFragmentPresenter.handleAudioClick(
      shouldEnableAudioPlayback = true, feedbackId = contentId
    )
  }

  override fun disableAudioPlayback() {
    audioFragmentPresenter.handleAudioClick(shouldEnableAudioPlayback = false, feedbackId = null)
  }

  override fun setStateAndExplorationId(newState: State, explorationId: String) =
    audioFragmentPresenter.setStateAndExplorationId(newState, explorationId)

  override fun loadMainContentAudio(allowAutoPlay: Boolean) {
    // This function is only called for new states loading their audio.
    audioFragmentPresenter.loadMainContentAudio(allowAutoPlay, reloadingContent = true)
  }

  override fun loadFeedbackAudio(contentId: String, allowAutoPlay: Boolean) =
    audioFragmentPresenter.loadFeedbackAudio(contentId, allowAutoPlay)

  override fun pauseAudio() {
    audioFragmentPresenter.pauseAudio()
  }

  override fun enableAudioWhileOnCellular(saveUserChoice: Boolean) =
    audioFragmentPresenter.handleEnableAudio(saveUserChoice)

  override fun disableAudioWhileOnCellular(saveUserChoice: Boolean) =
    audioFragmentPresenter.handleDisableAudio(saveUserChoice)

  /** Used in data binding to know if user is touching SeekBar. */
  override fun getUserIsSeeking() = audioFragmentPresenter.userIsSeeking

  /** Used in data binding to know position of user's touch. */
  override fun getUserPosition() = audioFragmentPresenter.userProgress
}
