package com.tapbi.spark.controlcenter.ui.main.focus.allowpeople

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
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

class AllowPeopleFragment :
    BaseBindingFragment<FragmentAllowPeopleBinding, AllowPeopleViewModel>() {
    private var peopleAdapter: PeopleAdapter? = null
    private var peopleSearchAdapter: PeopleSearchAdapter? = null
    private var focusiOS: FocusIOS? = null
    private var isSearch = false
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.rvPeopleSearch.visibility == View.VISIBLE || binding.tvNoData.visibility == View.VISIBLE) {
                    MethodUtils.hideKeyboard(requireActivity())
                    binding.searchView.clearFocus()
                    binding.searchView.setQuery("", false)
                    binding.tvTitle.text = getString(R.string.allowed_people)
                    binding.rvPeopleSearch.visibility = View.GONE
                    binding.rcAllowPeople.visibility = View.VISIBLE
                    binding.tvNoData.visibility = View.GONE
                } else {
//                    setEnabled(false);
                    mainViewModel.itemFocusDetail.postValue(focusiOS)
                    (requireActivity() as MainActivity).navControllerMain.popBackStack(
                        R.id.allowPeopleFragment,
                        true
                    )
                }
            }
        }
    private var topBinding: ItemAlsoAllowBinding? = null
    private var middleBinding: ItemMiddleAllowPeopleBinding? = null
    private val listPeople: MutableList<ItemPeople?> = mutableListOf()
    private val listSearchPeople: MutableList<ItemPeople?> = mutableListOf()
    private var listPeopleAllowed: MutableList<ItemPeople?> = mutableListOf()
    private val listPeopleFavourite: MutableList<ItemPeople?> = mutableListOf()
    private var textQuery: String? = ""
    override fun getViewModel(): Class<AllowPeopleViewModel> {
        return AllowPeopleViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_allow_people

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
        setUpPaddingStatusBar(binding.layoutAllowPeople)
        (requireActivity() as MainActivity).setColorNavigation(R.color.color_F2F2F6)
        if (savedInstanceState?.getString(Constant.ITEM_FOCUS_PEOPLE) != null) {
            focusiOS = Gson().fromJson(
                savedInstanceState.getString(Constant.ITEM_FOCUS_PEOPLE),
                object : TypeToken<FocusIOS?>() {}.type
            )
            textQuery = savedInstanceState.getString(Constant.TEXT_QUERY_ALLOWED_PEOPLE, "")
            mainViewModel.itemFocusCurrentPeople.postValue(focusiOS)
        } else {
            viewModel.getFavoritePeople(requireContext())
        }
        observerData()
        initView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constant.ITEM_FOCUS_PEOPLE, Gson().toJson(focusiOS))
        if (!textQuery.isNullOrEmpty()) {
            outState.putString(Constant.TEXT_QUERY_ALLOWED_PEOPLE, textQuery)
        }
    }

    private fun observerData() {
        mainViewModel.itemFocusCurrentPeople.observe(viewLifecycleOwner) { focusIOS: FocusIOS? ->
            if (focusIOS != null) {
                focusiOS = focusIOS
                mainViewModel.getAllowedPeopleByName(focusIOS.name)
                mainViewModel.itemFocusCurrentPeople.postValue(null)
            }
        }
        viewModel.listAllPeopleLiveData.observe(viewLifecycleOwner) { itemPeople: MutableList<ItemPeople>? ->
            if (itemPeople != null) {
                listPeople.clear()
                listPeople.addAll(itemPeople)
                isSearch = true
                if (textQuery != null && !textQuery.isNullOrEmpty()) {
                    binding.searchView.setQuery(textQuery, true)
                }
                switchUpdate()
            }
        }
        mainViewModel.listItemAllowedPeople.observe(viewLifecycleOwner) { itemPeople ->
            if (itemPeople != null) {
                listPeopleAllowed = itemPeople.toMutableList()
                viewModel.getAllPeople(requireContext())
                mainViewModel.listItemAllowedPeople.postValue(null)
            }
        }
        viewModel.listFavoritePeopleLiveData.observe(viewLifecycleOwner) { itemPeople: List<ItemPeople?>? ->
            if (itemPeople != null) {
                listPeopleFavourite.clear()
                listPeopleFavourite.addAll(itemPeople)
            }
        }
        viewModel.deleteItemAllowedPeopleLiveData.observe(viewLifecycleOwner) { aBoolean: Boolean? ->
            if (aBoolean == true) {
                for (item in listPeopleAllowed) {
                    if (item?.nameFocus == focusiOS?.name) {
                        viewModel.insertAllowedPeople(item)
                    }
                }
                //                mainViewModel.listItemAllowedPeople.postValue(listPeopleAllowed);
            }
        }
    }

    private fun switchUpdate() {
        when (focusiOS?.modeAllowPeople) {
            Constant.EVERY_ONE -> {
                setSwitchMode(every = true, noOne = false, favourite = false, allContact = false)
                setListAllAllowedPeople(isStart = true, updateFocus = false)
            }

            Constant.NO_ONE -> {
                setSwitchMode(every = false, noOne = true, favourite = false, allContact = false)
                for (itemPeopleAllowed in listPeopleAllowed) {
                    for (itemPeople in listPeople) {
                        if (itemPeople?.contactId == itemPeopleAllowed!!.contactId) {
                            itemPeople?.isStart = true
                            break
                        }
                    }
                }
                peopleAdapter?.setData(focusiOS, listPeople)
            }

            Constant.FAVOURITE -> {
                setSwitchMode(every = false, noOne = false, favourite = true, allContact = false)
                updateFavourite(false)
            }

            Constant.ALL_CONTACT -> {
                setSwitchMode(every = false, noOne = false, favourite = false, allContact = true)
                setListAllAllowedPeople(isStart = true, updateFocus = false)
            }
        }
        countStartRemove()
    }

    @SuppressLint("SetTextI18n")
    private fun countStartRemove() {
        var count = 0
        for (itemPeople in listPeople) {
            if (itemPeople?.isStart ==true) {
                count++
            }
        }
        if (middleBinding != null) {
            middleBinding?.tvRemove?.text = getString(R.string.remove_allow) + " " + count + " )"
        }
    }

    private fun initView() {
        initAdapter()
        initListener()
    }

    private fun initListener() {
        hideKeyBoardScrollRV(binding.rvPeopleSearch)
        backPress()
        initSearchView()
    }

    private fun backPress() {
        binding.imBack.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            if (binding.rvPeopleSearch.visibility == View.VISIBLE || binding.tvNoData.visibility == View.VISIBLE) {
                MethodUtils.hideKeyboard(requireActivity())
                binding.searchView.clearFocus()
                binding.searchView.setQuery("", false)
                binding.tvTitle.text = getString(R.string.allowed_people)
                binding.rvPeopleSearch.visibility = View.GONE
                binding.rcAllowPeople.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
            } else {
                mainViewModel.itemFocusDetail.postValue(focusiOS)
                (requireActivity() as MainActivity).navControllerMain.popBackStack(
                    R.id.allowPeopleFragment,
                    true
                )
            }
        }
    }

    private fun initSearchView() {
        binding.searchView.setOnQueryTextFocusChangeListener { _: View?, hasFocus: Boolean ->
            if (hasFocus) {
                binding.rcAllowPeople.scrollToPosition(0)
                binding.rvPeopleSearch.scrollToPosition(0)
                binding.rvPeopleSearch.visibility = View.VISIBLE
                binding.rcAllowPeople.visibility = View.GONE
                binding.tvTitle.text = getString(R.string.contact)
                if (textQuery!!.isEmpty()) {
                    listSearchPeople.clear()
                    listSearchPeople.addAll(listPeople)
                    peopleSearchAdapter?.setData(listSearchPeople)
                }
            }
        }
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (isSearch) {
                    setListSearchPeople(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                textQuery = newText
                if (isSearch) {
                    setListSearchPeople(newText)
                }
                return true
            }
        })
    }

    private fun setListSearchPeople(newText: String) {
        listSearchPeople.clear()
        if (newText.isEmpty()) {
            listSearchPeople.addAll(listPeople)
            peopleSearchAdapter?.setData(listSearchPeople)
            binding.rvPeopleSearch.visibility = View.VISIBLE
            if (listSearchPeople.size > 0) {
                binding.tvNoData.visibility = View.GONE
            } else {
                binding.tvNoData.visibility = View.VISIBLE
            }
        } else {
            for (itemPeople in listPeople) {
                if (itemPeople?.name?.uppercase()?.contains(newText.uppercase()) ==true) {
                    listSearchPeople.add(itemPeople)
                }
            }
            peopleSearchAdapter?.setData(listSearchPeople)
            if (listSearchPeople.size > 0) {
                binding.rvPeopleSearch.visibility = View.VISIBLE
                binding.tvNoData.visibility = View.GONE
            } else {
                binding.rvPeopleSearch.visibility = View.GONE
                binding.tvNoData.visibility = View.VISIBLE
            }
        }
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
                    setTextTvAllowIncomingCalls(getString(R.string.Allow_incoming_calls_from_only_the_contacts))
                    setListAllAllowedPeople(isStart = false, updateFocus = true)
                }
            }

            override fun clickSwitch(position: Int, isStart: Boolean) {
                listPeople[position - 2]!!.isStart = !isStart
                focusiOS?.modeAllowPeople = Constant.NO_ONE
                peopleAdapter?.setData(focusiOS, listPeople)
                setSwitchMode(every = false, noOne = true, favourite = false, allContact = false)
                setTextTvAllowIncomingCalls(getString(R.string.Allow_incoming_calls_from_only_the_contacts))
                updateItemFocusHomeFragment(listPeople, !isStart, position - 2)
            }
        })
        peopleSearchAdapter?.setListener { position: Int, isStart: Boolean ->
            if (position == RecyclerView.NO_POSITION || position >= listSearchPeople.size) {
                return@setListener
            }
            listSearchPeople.size
            listSearchPeople[position]!!.isStart = !isStart
            peopleSearchAdapter?.notifyItemChanged(position, listSearchPeople)
            focusiOS?.modeAllowPeople = Constant.NO_ONE
            peopleAdapter?.setData(focusiOS, listPeople)
            setSwitchMode(every = false, noOne = true, favourite = false, allContact = false)
            setTextTvAllowIncomingCalls(getString(R.string.Allow_incoming_calls_from_only_the_contacts))
            updateItemFocusHomeFragment(listSearchPeople, !isStart, position)
        }
    }

    private fun switchListener() {
        topBinding?.viewAlsoAllow?.swAllContact?.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            if (focusiOS?.modeAllowPeople == Constant.ALL_CONTACT) {
                focusiOS?.modeAllowPeople = Constant.NO_ONE
                setSwitchMode(every = false, noOne = true, favourite = false, allContact = false)
                setTextTvAllowIncomingCalls(getString(R.string.Allow_incoming_calls_from_only_the_contacts))
            } else {
                focusiOS?.modeAllowPeople = Constant.ALL_CONTACT
                setSwitchMode(every = false, noOne = false, favourite = false, allContact = true)
                setListAllAllowedPeople(isStart = true, updateFocus = true)
                setTextTvAllowIncomingCalls(getString(R.string.Allow_incoming_calls_from_your_contacts))
            }
        }
        topBinding?.viewAlsoAllow?.swEveryone?.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            if (focusiOS?.modeAllowPeople == Constant.EVERY_ONE) {
                focusiOS?.modeAllowPeople = Constant.NO_ONE
                setSwitchMode(every = false, noOne = true, favourite = false, allContact = false)
                setTextTvAllowIncomingCalls(getString(R.string.Allow_incoming_calls_from_only_the_contacts))
            } else {
                focusiOS?.modeAllowPeople = Constant.EVERY_ONE
                setSwitchMode(every = true, noOne = false, favourite = false, allContact = false)
                setListAllAllowedPeople(isStart = true, updateFocus = true)
                setTextTvAllowIncomingCalls(getString(R.string.Allow_incoming_calls_from_everyone))
            }
        }
        topBinding?.viewAlsoAllow?.swFavorite?.setOnClickListener { v: View? ->
            ViewHelper.preventTwoClick(v)
            if (focusiOS?.modeAllowPeople == Constant.FAVOURITE) {
                focusiOS?.modeAllowPeople = Constant.NO_ONE
                setSwitchMode(every = false, noOne = true, favourite = false, allContact = false)
                setTextTvAllowIncomingCalls(getString(R.string.Allow_incoming_calls_from_only_the_contacts))
            } else {
                focusiOS?.modeAllowPeople = Constant.FAVOURITE
                setSwitchMode(every = false, noOne = false, favourite = true, allContact = false)
                updateFavourite(true)
                setTextTvAllowIncomingCalls(getString(R.string.allow_incoming_calls_from_only_the_contacts_you_added_to_the_focus_and_your_favorites))
            }
        }
        topBinding?.viewAlsoAllow?.swNoOne?.setOnClickListener {
            focusiOS?.modeAllowPeople = Constant.NO_ONE
            setSwitchMode(every = false, noOne = true, favourite = false, allContact = false)
            setTextTvAllowIncomingCalls(getString(R.string.Allow_incoming_calls_from_only_the_contacts))
        }
    }

    private fun setListAllAllowedPeople(isStart: Boolean, updateFocus: Boolean) {
        listPeopleAllowed.clear()
        for (itemPeople in listPeople) {
            itemPeople?.isStart = isStart
            if (isStart) {
                itemPeople?.nameFocus = focusiOS?.name
                listPeopleAllowed.add(itemPeople)
            }
        }
        peopleAdapter?.setData(focusiOS, listPeople)
        if (textQuery!!.isEmpty()) peopleSearchAdapter?.setData(listPeople)
        if (updateFocus) {
            updateFocusHome()
        }
    }

    private fun updateFocusHome() {
        countStartRemove()
        viewModel.deleteItemAllowedPeople(focusiOS?.name)
        focusiOS?.modeAllowPeople?.let { viewModel.updateFocusIOS(it, focusiOS?.name) }
    }

    private fun updateItemFocusHomeFragment(
        list: List<ItemPeople?>,
        isStart: Boolean,
        position: Int
    ) {
        val itemPeople = list[position]
        if (isStart) {
            if (itemPeople != null){
                itemPeople.nameFocus = focusiOS?.name
                viewModel.insertAllowedPeople(
                    ItemPeople(
                        itemPeople.contactId,
                        itemPeople.name,
                        itemPeople.phone,
                        itemPeople.image,
                        itemPeople.isStart,
                        itemPeople.nameFocus
                    )
                )
            }
            listPeopleAllowed.add(itemPeople)
        } else {
            for (itemPeopleAllowed in listPeopleAllowed) {
                if (itemPeopleAllowed!!.contactId == itemPeople?.contactId) {
                    listPeopleAllowed.remove(itemPeopleAllowed)
                    break
                }
            }
        }
        updateFocusHome()
    }

    private fun updateFavourite(isInsert: Boolean) {
        if (focusiOS?.modeAllowPeople == Constant.FAVOURITE) {
            for (itemPeople in listPeople) {
                itemPeople?.isStart = false
            }
            for (itemPeopleFavorite in listPeopleFavourite) {
                for (itemPeople in listPeople) {
                    if (itemPeople?.contactId == itemPeopleFavorite!!.contactId) {
                        itemPeople?.isStart = true
                        break
                    }
                }
            }
            peopleAdapter?.setData(focusiOS, listPeople)
            //            if (querySearch.isEmpty())
//                itemSearchPeopleAdapter ?.setData(listPeople)
            listPeopleAllowed.clear()
            for (itemPeople in listPeople) {
                if (itemPeople?.isStart == true) {
                    listPeopleAllowed.add(itemPeople)
                }
            }
            for (itemPeople in listPeopleAllowed) {
                itemPeople?.nameFocus = focusiOS?.name
                //                viewModel.insertAllowedPeople(itemPeople);
            }
            if (isInsert) {
                updateFocusHome()
            }
        }
    }

    private fun setSwitchMode(
        every: Boolean,
        noOne: Boolean,
        favourite: Boolean,
        allContact: Boolean
    ) {
        if (topBinding != null) {
            topBinding?.viewAlsoAllow?.swEveryone?.setImageResource(if (every) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
            topBinding?.viewAlsoAllow?.swNoOne?.setImageResource(if (noOne) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
            topBinding?.viewAlsoAllow?.swFavorite?.setImageResource(if (favourite) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
            topBinding?.viewAlsoAllow?.swAllContact?.setImageResource(if (allContact) R.drawable.ic_switch_on else R.drawable.ic_switch_off)
        }
    }

    private fun setTextTvAllowIncomingCalls(content: String) {
        if (topBinding != null) {
            topBinding?.tvAllowIncomingCalls?.text = content
        }
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