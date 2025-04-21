package com.tapbi.spark.controlcenter.ui.main.focus.createfocus.allowedpeople

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.adapter.PeopleAdapter
import com.tapbi.spark.controlcenter.adapter.PeopleAdapter.IPeopleAllow
import com.tapbi.spark.controlcenter.adapter.PeopleSearchAdapter
import com.tapbi.spark.controlcenter.common.Constant
import com.tapbi.spark.controlcenter.common.models.MessageEvent
import com.tapbi.spark.controlcenter.data.model.FocusIOS
import com.tapbi.spark.controlcenter.data.model.ItemPeople
import com.tapbi.spark.controlcenter.databinding.FragmentAllowPeopleBinding
import com.tapbi.spark.controlcenter.databinding.ItemAlsoAllowBinding
import com.tapbi.spark.controlcenter.databinding.ItemMiddleAllowPeopleBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment
import com.tapbi.spark.controlcenter.ui.main.MainActivity
import com.tapbi.spark.controlcenter.utils.MethodUtils
import com.tapbi.spark.controlcenter.utils.helper.ViewHelper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class CustomAllowedPeopleFragment :
    BaseBindingFragment<FragmentAllowPeopleBinding, CustomAllowedPeopleViewModel>() {
    private var peopleAdapter: PeopleAdapter? = null
    private var peopleSearchAdapter: PeopleSearchAdapter? = null
    private var focusiOS: FocusIOS? = null
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.rvPeopleSearch.visibility == VISIBLE || binding.tvNoData.visibility == VISIBLE) {
                    MethodUtils.hideKeyboard(requireActivity())
                    //                binding.searchView.clearFocus();
//                binding.searchView.setQuery("", false);
                    binding.tvTitle.text = getString(R.string.allowed_people)
                    binding.rvPeopleSearch.visibility = GONE
                    binding.rcAllowPeople.visibility = VISIBLE
                    binding.tvNoData.visibility = GONE
                } else {
                    isEnabled = false
                    mainViewModel.itemFocusDetail.postValue(focusiOS)
                    //                    onBackPressed();
                    (requireActivity() as MainActivity).navControllerMain.popBackStack(
                        R.id.customAllowedPeopleFragment, true
                    )
                }
            }
        }
    private var topBinding: ItemAlsoAllowBinding? = null
    private var middleBinding: ItemMiddleAllowPeopleBinding? = null
    private val listPeople: MutableList<ItemPeople> = ArrayList()
    private val listSearchPeople: MutableList<ItemPeople> = mutableListOf()
    private var listPeopleIStart: MutableList<ItemPeople> = mutableListOf()
    private val listPeopleFavourite: MutableList<ItemPeople> = mutableListOf()
    private var textQuery = ""
    override fun getViewModel(): Class<CustomAllowedPeopleViewModel> {
        return CustomAllowedPeopleViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_allow_people

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        initView()
        observerData()
        if (savedInstanceState != null) {
            focusiOS = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_CUSTOM_ALLOWED_PEOPLE),
                object : TypeToken<FocusIOS?>() {}.type
            )
            listPeopleIStart = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_CUSTOM_ALLOWED_PEOPLE_START),
                object : TypeToken<List<ItemPeople?>?>() {}.type
            )
            textQuery = savedInstanceState.getString(Constant.TEXT_QUERY_CUSTOM_ALLOWED_PEOPLE, "")
            mainViewModel.itemCreateFocusCurrent.postValue(focusiOS)
        } else {
            viewModel.getFavoritePeople(requireContext())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constant.ITEM_CUSTOM_ALLOWED_PEOPLE, Gson().toJson(focusiOS))
        outState.putString(
            Constant.ITEM_CUSTOM_ALLOWED_PEOPLE_START, Gson().toJson(listPeopleIStart)
        )
        if (textQuery.isNotEmpty()) {
            outState.putString(Constant.TEXT_QUERY_CUSTOM_ALLOWED_PEOPLE, textQuery)
        }
    }

    private fun observerData() {
        mainViewModel.itemCreateFocusCurrent.observe(viewLifecycleOwner) { focusIOS: FocusIOS? ->
            if (focusIOS != null) {
                focusiOS = focusIOS
                viewModel.getAllPeople(requireContext())
                viewModel.getFavoritePeople(requireContext())
            }
        }
        viewModel.listAllPeopleLiveData.observe(viewLifecycleOwner) { itemPeople ->
            if (itemPeople != null) {
                listPeople.clear()
                listPeople.addAll(itemPeople)
                switchUpdate()
            }
        }
        viewModel.listFavoritePeopleLiveData.observe(viewLifecycleOwner) { itemPeople ->
            if (itemPeople != null) {
                listPeopleFavourite.clear()
                listPeopleFavourite.addAll(itemPeople)
            }
        }
    }

    private fun initView() {
        setUpPaddingStatusBar(binding.layoutAllowPeople)
        (requireActivity() as MainActivity).setColorNavigation(R.color.color_F2F2F6)
        initAdapter()
        initListener()
        binding.viewClickAllow.root.visibility = VISIBLE
        binding.viewClickAllow.tvAllow.visibility = VISIBLE
        binding.viewClickAllow.tvAllowGone.visibility = INVISIBLE
    }

    private fun initListener() {
        hideKeyBoardScrollRV(binding.rvPeopleSearch)
        backPress()
        initSearchView()
        binding.viewClickAllow.tvAllow.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            mainViewModel.itemCreateAppFocusCurrent.postValue(focusiOS)
            mainViewModel.listItemPeopleStart.postValue(listPeopleIStart)
            (requireActivity() as MainActivity).navigate(
                R.id.action_customAllowedPeopleFragment_to_customNewAllowedAppFragment,
                R.id.customAllowedPeopleFragment
            )
        }
        binding.viewClickAllow.tvAllowNone.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            setListAllAllowedPeople(false)
            focusiOS?.modeAllowPeople = Constant.NO_ONE
            mainViewModel.itemCreateAppFocusCurrent.postValue(focusiOS)
            mainViewModel.listItemPeopleStart.postValue(listPeopleIStart)
            (requireActivity() as MainActivity).navigate(
                R.id.action_customAllowedPeopleFragment_to_customNewAllowedAppFragment,
                R.id.customAllowedPeopleFragment
            )
        }
    }

    private fun initSearchView() {
        binding.searchView.setOnQueryTextFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus) {
                binding.rcAllowPeople.scrollToPosition(0)
                binding.rvPeopleSearch.scrollToPosition(0)
                binding.rvPeopleSearch.visibility = VISIBLE
                binding.rcAllowPeople.visibility = GONE
                binding.tvTitle.text = getString(R.string.contact)
                if (textQuery.isEmpty()) {
                    listSearchPeople.clear()
                    listSearchPeople.addAll(listPeople)
                    peopleSearchAdapter?.setData(listSearchPeople)
                }
            }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (binding.searchView.hasFocus()) {
                    textQuery = newText
                    listSearchPeople.clear()
                    if (newText.isEmpty()) {
                        listSearchPeople.addAll(listPeople)
                        peopleSearchAdapter?.setData(listSearchPeople)
                        binding.rvPeopleSearch.visibility = VISIBLE
                        binding.tvNoData.visibility = GONE
                    } else {
                        for (itemPeople in listPeople) {
                            if (itemPeople.name.uppercase().contains(newText.uppercase())) {
                                listSearchPeople.add(itemPeople)
                            }
                        }
                        if (listSearchPeople.size > 0) {
                            peopleSearchAdapter?.setData(listSearchPeople)
                            binding.rvPeopleSearch.visibility = VISIBLE
                            binding.tvNoData.visibility = GONE
                        } else {
                            binding.rvPeopleSearch.visibility = GONE
                            binding.tvNoData.visibility = VISIBLE
                        }
                    }
                }
                return true
            }
        })
    }

    private fun switchUpdate() {
        Timber.e("hoangld getModeAllowPeople: " + focusiOS?.modeAllowPeople + " textQuery " + textQuery)
        when (focusiOS?.modeAllowPeople) {
            Constant.EVERY_ONE -> {
                setSwitchMode(true, noOne = false, favourite = false, allContact = false)
                setListAllAllowedPeople(true)
            }

            Constant.NO_ONE -> {
                setSwitchMode(every = false, noOne = true, favourite = false, allContact = false)
                peopleAdapter?.setData(focusiOS, listPeople)
                if (textQuery.isEmpty()) {
                    peopleSearchAdapter?.setData(listPeople)
                }
            }

            Constant.FAVOURITE -> {
                setSwitchMode(every = false, noOne = false, favourite = true, allContact = false)
                updateFavourite()
            }

            Constant.ALL_CONTACT -> {
                setSwitchMode(every = false, noOne = false, favourite = false, allContact = true)
                setListAllAllowedPeople(true)
            }
        }
        if (listPeopleIStart.size > 0) {
            for (itemStart in listPeopleIStart) {
                for (itemPeople in listPeople) {
                    if (itemStart.contactId == itemPeople.contactId) {
                        itemPeople.isStart = true
                        break
                    }
                }
            }
        }
        countStartRemove()
        peopleAdapter?.notifyDataSetChanged()
    }

    private fun backPress() {
        binding.imBack.setOnClickListener { v: View? -> requireActivity().onBackPressed() }
    }

    private fun initAdapter() {
        peopleAdapter = PeopleAdapter()
        binding.rcAllowPeople.adapter = peopleAdapter
        peopleSearchAdapter = PeopleSearchAdapter()
        binding.rvPeopleSearch.adapter = peopleSearchAdapter
        peopleAdapter?.setListener(object : IPeopleAllow {
            override fun topType(binding: ItemAlsoAllowBinding) {
                topBinding = binding
                switchListener()
            }

            override fun middleType(binding: ItemMiddleAllowPeopleBinding) {
                middleBinding = binding
                countStartRemove()
                middleBinding?.tvRemove?.setOnClickListener { v: View? ->
                    ViewHelper.preventTwoClick(v)
                    focusiOS?.modeAllowPeople = Constant.NO_ONE
                    setSwitchMode(
                        every = false,
                        noOne = true,
                        favourite = false,
                        allContact = false
                    )
                    if (topBinding != null) {
                        topBinding?.tvAllowIncomingCalls?.text =
                            getString(R.string.Allow_incoming_calls_from_only_the_contacts)
                    }
                    setListAllAllowedPeople(false)
                }
            }

            override fun clickSwitch(position: Int, isStart: Boolean) {
                listPeople[position - 2].isStart = !isStart
                if (listPeople[position - 2].isStart) {
                    listPeopleIStart.add(listPeople[position - 2])
                } else {
                    listPeopleIStart.remove(listPeople[position - 2])
                }
                focusiOS?.modeAllowPeople = Constant.NO_ONE
                peopleAdapter?.setData(focusiOS, listPeople)
                setSwitchMode(false, noOne = true, favourite = false, allContact = false)
                if (topBinding != null) {
                    topBinding?.tvAllowIncomingCalls?.text =
                        getString(R.string.Allow_incoming_calls_from_only_the_contacts)
                }
            }
        })
        peopleSearchAdapter?.setListener { position: Int, isStart: Boolean ->
            if (position > RecyclerView.NO_POSITION && position < listSearchPeople.size) {
                listSearchPeople[position].isStart = !isStart
                peopleSearchAdapter?.notifyItemChanged(position, listSearchPeople)
                if (listSearchPeople[position].isStart) {
                    listPeopleIStart.add(listSearchPeople[position])
                } else {
                    listPeopleIStart.remove(listSearchPeople[position])
                }
                focusiOS?.modeAllowPeople = Constant.NO_ONE
                peopleAdapter?.setData(focusiOS, listPeople)
                setSwitchMode(every = false, noOne = true, favourite = false, allContact = false)
                if (topBinding != null) {
                    topBinding?.tvAllowIncomingCalls?.text =
                        getString(R.string.Allow_incoming_calls_from_only_the_contacts)
                }
            }
        }
    }

    private fun setSwitchMode(
        every: Boolean, noOne: Boolean, favourite: Boolean, allContact: Boolean
    ) {
        if (topBinding != null) {
            topBinding?.viewAlsoAllow?.swEveryone?.setImageResource(if (every) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
            topBinding?.viewAlsoAllow?.swNoOne?.setImageResource(if (noOne) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
            topBinding?.viewAlsoAllow?.swFavorite?.setImageResource(if (favourite) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
            topBinding?.viewAlsoAllow?.swAllContact?.setImageResource(if (allContact) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
        }
    }

    private fun switchListener() {
        if (topBinding == null) {
            return
        }
        topBinding?.viewAlsoAllow?.swAllContact?.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            if (focusiOS?.modeAllowPeople == Constant.ALL_CONTACT) {
                focusiOS?.modeAllowPeople = Constant.NO_ONE
                setSwitchMode(every = false, noOne = true, favourite = false, allContact = false)
                topBinding?.tvAllowIncomingCalls?.text =
                    getString(R.string.Allow_incoming_calls_from_only_the_contacts)
            } else {
                focusiOS?.modeAllowPeople = Constant.ALL_CONTACT
                setSwitchMode(every = false, noOne = false, favourite = false, allContact = true)
                setListAllAllowedPeople(true)
                topBinding?.tvAllowIncomingCalls?.text =
                    getString(R.string.Allow_incoming_calls_from_your_contacts)
            }
        }
        topBinding?.viewAlsoAllow?.swEveryone?.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            if (focusiOS?.modeAllowPeople == Constant.EVERY_ONE) {
                focusiOS?.modeAllowPeople = Constant.NO_ONE
                setSwitchMode(false, noOne = true, favourite = false, allContact = false)
                topBinding?.tvAllowIncomingCalls?.text =
                    getString(R.string.Allow_incoming_calls_from_only_the_contacts)
            } else {
                focusiOS?.modeAllowPeople = Constant.EVERY_ONE
                setSwitchMode(true, noOne = false, favourite = false, allContact = false)
                setListAllAllowedPeople(true)
                topBinding?.tvAllowIncomingCalls?.text =
                    getString(R.string.Allow_incoming_calls_from_everyone)
            }
        }
        topBinding?.viewAlsoAllow?.swFavorite?.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            if (focusiOS?.modeAllowPeople == Constant.FAVOURITE) {
                focusiOS?.modeAllowPeople = Constant.NO_ONE
                setSwitchMode(false, noOne = true, favourite = false, allContact = false)
                topBinding?.tvAllowIncomingCalls?.text =
                    getString(R.string.Allow_incoming_calls_from_only_the_contacts)
            } else {
                focusiOS?.modeAllowPeople = Constant.FAVOURITE
                setSwitchMode(false, noOne = false, favourite = true, allContact = false)
                updateFavourite()
                topBinding?.tvAllowIncomingCalls?.text =
                    getString(R.string.allow_incoming_calls_from_only_the_contacts_you_added_to_the_focus_and_your_favorites)
            }
        }
        topBinding?.viewAlsoAllow?.swNoOne?.setOnClickListener {
            focusiOS?.modeAllowPeople = Constant.NO_ONE
            setSwitchMode(false, noOne = true, favourite = false, allContact = false)
            topBinding?.tvAllowIncomingCalls?.text =
                getString(R.string.Allow_incoming_calls_from_only_the_contacts)
        }
    }

    private fun setListAllAllowedPeople(isStart: Boolean) {
        listPeopleIStart.clear()
        for (it in listPeople) {
            it.isStart = isStart
            if (isStart) {
                listPeopleIStart.add(it)
            }
        }
        peopleAdapter?.setData(focusiOS, listPeople)
        if (textQuery.isEmpty()) peopleSearchAdapter?.setData(listPeople)
    }

    @SuppressLint("SetTextI18n")
    private fun countStartRemove() {
        var count = 0
        for (itemPeople in listPeople) {
            if (itemPeople.isStart) {
                count++
            }
        }
        if (middleBinding != null) {
            middleBinding?.tvRemove?.text = getString(R.string.remove_allow) + " " + count + " )"
        }
    }

    private fun updateFavourite() {
        listPeopleIStart.clear()
        for (it in listPeople) {
            it.isStart = false
        }
        for (itFavorite in listPeopleFavourite) {
            for (it in listPeople) {
                if (it.contactId == itFavorite.contactId) {
                    it.isStart = true
                    listPeopleIStart.add(it)
                    break
                }
            }
        }
        peopleAdapter?.setData(focusiOS, listPeople)
        if (textQuery.isEmpty()) peopleSearchAdapter?.setData(listPeople)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(messageEvent: MessageEvent) {
        when (messageEvent.typeEvent) {
            Constant.CONTACT_CHANGE, Constant.CONTACT_DELETE -> {
                viewModel.getAllPeople(requireContext())
                viewModel.getFavoritePeople(requireContext())
            }
        }
    }

    override fun onPermissionGranted() {}
    override fun onDestroy() {
        super.onDestroy()
        onBackPressedCallback.remove()
    }
}