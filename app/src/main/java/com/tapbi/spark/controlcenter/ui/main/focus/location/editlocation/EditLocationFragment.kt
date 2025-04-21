package com.tapbi.spark.controlcenter.ui.main.focus.location.editlocation

import android.os.Bundle
import android.view.View
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.databinding.FragmentLocationBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment

//import static android.Manifest.permission.ACCESS_FINE_LOCATION;
class EditLocationFragment :
    BaseBindingFragment<FragmentLocationBinding, EditLocationViewModel>() {
    //    public boolean isShowMap = false;
    //    public boolean isSearch = false;
    //    private FusedLocationProviderClient fusedLocationProviderClient;
    //    private FocusIOS focusIOS;
    //    private ItemTurnOn itemTurnOnLocation;
    //    private GoogleMap map;
    //    private String textQuery = "";
    //    private Address address;
    //    private Marker marker;
    //    private Circle circle;
    //    private String nameLocation = "";
    //    private List<ItemTurnOn> listTurnOn = new ArrayList<>();
    //    private  OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
    //        @Override
    //        public void handleOnBackPressed() {
    //            MethodUtils.hideKeyboard(requireActivity());
    //            mainViewModel.itemFocusDetail.postValue(focusIOS);
    //            ((MainActivity) requireActivity()).navControllerMain.popBackStack(R.id.editlocationFragment, true);
    //        }
    //    };
    override fun getViewModel(): Class<EditLocationViewModel> {
        return EditLocationViewModel::class.java
    }

    override val layoutId: Int
        get() = R.layout.fragment_location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
//        initView();
//        observerData();
//        if (savedInstanceState != null) {
//            focusIOS = new Gson().fromJson(savedInstanceState.getString(Constant.ITEM_EDIT_LOCATION_FOCUS), new TypeToken<FocusIOS>() {
//            }.getType());
//            itemTurnOnLocation = new Gson().fromJson(savedInstanceState.getString(Constant.ITEM_EDIT_AUTO_LOCATION_FOCUS), new TypeToken<ItemTurnOn>() {
//            }.getType());
//            mainViewModel.editItemAutomationFocus.postValue(focusIOS);
//            mainViewModel.itemAutomationFocus.postValue(itemTurnOnLocation);
//        }
    }

    //    @Override
    //    public void onSaveInstanceState(@NonNull Bundle outState) {
    //        super.onSaveInstanceState(outState);
    //        outState.putString(Constant.ITEM_EDIT_LOCATION_FOCUS, new Gson().toJson(focusIOS));
    //        outState.putString(Constant.ITEM_EDIT_AUTO_LOCATION_FOCUS, new Gson().toJson(itemTurnOnLocation));
    //    }
    //
    //    @Override
    //    public void onResume() {
    //        super.onResume();
    //
    //    }
    //    private void observerData() {
    //        mainViewModel.editItemAutomationFocus.observe(getViewLifecycleOwner(), focusIOS -> {
    //            if (focusIOS != null) {
    //                this.focusIOS = focusIOS;
    //                mainViewModel.getListAutomationByFocus(focusIOS.getName());
    //            }
    //        });
    //        mainViewModel.itemAutomationFocus.observe(getViewLifecycleOwner(), itemTurnOn -> {
    //            if (itemTurnOn != null) {
    //                itemTurnOnLocation = itemTurnOn;
    //                isSearch = true;
    //                binding.searchView.setQuery(itemTurnOn.getNameLocation(), true);
    //
    //            }
    //        });
    //        mainViewModel.listAutomationMutableLiveData.observe(getViewLifecycleOwner(), turnOnList -> {
    //            if (turnOnList != null) {
    //                listTurnOn.clear();
    //                listTurnOn.addAll(turnOnList);
    //            }
    //        });
    //        mainViewModel.internetConnectedSearch.observe(getViewLifecycleOwner(), aBoolean -> {
    //            if (aBoolean != null) {
    //                if (aBoolean) {
    //                    if (!textQuery.isEmpty()) {
    //                        viewModel.getMap(requireContext(), textQuery);
    //                        binding.tvCurrent.setVisibility(View.GONE);
    //                        if (isShowMap) {
    //                            binding.frameMap.setVisibility(View.VISIBLE);
    //                            binding.viewItemLocation.imDone.setVisibility(View.VISIBLE);
    //                        }
    //                    } else {
    //                        binding.viewItemLocation.getRoot().setVisibility(View.GONE);
    //                        binding.tvDone.setVisibility(View.GONE);
    //                    }
    //                } else {
    //                    toastText(R.string.error_internet);
    //                }
    //                isSearch = false;
    //                binding.progressCircularApp.setVisibility(View.GONE);
    //                mainViewModel.internetConnectedSearch.postValue(null);
    //            }
    //
    //        });
    //        viewModel.addressMutableLiveData.observe(getViewLifecycleOwner(), addresses -> {
    //            if (addresses != null) {
    //                address = addresses;
    //                nameLocation = address.getAddressLine(0).substring(0, address.getAddressLine(0).indexOf(","));
    //                binding.viewItemLocation.tvAddress.setText(addresses.getAddressLine(0));
    //                binding.viewItemLocation.tvTitleAddress.setText(nameLocation);
    //                binding.viewItemLocation.getRoot().setVisibility(View.VISIBLE);
    //                binding.viewItemLocation.imDone.setVisibility(View.INVISIBLE);
    //                drawCircle(addresses.getLatitude(), addresses.getLongitude());
    //                if (isShowMap) {
    //                    MethodUtils.hideKeyboard(requireActivity());
    //                    binding.frameMap.setVisibility(View.VISIBLE);
    //                    binding.viewItemLocation.imDone.setVisibility(View.VISIBLE);
    //                    binding.tvDone.setVisibility(View.VISIBLE);
    //                } else {
    //                    binding.frameMap.setVisibility(View.GONE);
    //                }
    //
    //            } else {
    //                toastText(R.string.error_no_found);
    //            }
    //            binding.progressCircularApp.setVisibility(View.GONE);
    //        });
    //        mainViewModel.internetConnected.observe(getViewLifecycleOwner(), aBoolean -> {
    //            if (aBoolean != null) {
    //                if (aBoolean) {
    //                    if (ActivityCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
    //                            && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
    //                            != PackageManager.PERMISSION_GRANTED) {
    //                        return;
    //                    }
    //                    map.setMyLocationEnabled(false);
    //                    map.getUiSettings().setMyLocationButtonEnabled(false);
    //                    CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
    //                    fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
    //                            cancellationTokenSource.getToken()).addOnSuccessListener(
    //                            location -> {
    //                                if (location != null) {
    //                                    // Logic to handle location object
    //                                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
    //                                    try {
    //                                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
    //                                        if (addresses != null && addresses.size() > 0) {
    //                                            String address = addresses.get(0).getAddressLine(0);
    //                                            binding.searchView.setQuery(address, true);
    //                                        }
    //                                    } catch (IOException e) {
    //                                        e.printStackTrace();
    //                                    }
    //                                }
    //                            });
    //
    //                } else {
    //                    toastText(R.string.error_internet);
    //                    binding.progressCircularApp.setVisibility(View.GONE);
    //                }
    //                mainViewModel.internetConnected.postValue(null);
    //            }
    //
    //        });
    //    }
    //
    //    private void initView() {
    //        setUpPaddingStatusBar(binding.layoutLocation);
    //        ((MainActivity) requireActivity()).setColorNavigation(R.color.color_F2F2F6);
    //        setupMap();
    //        searchView();
    //        initListener();
    //    }
    //
    //    private void searchView() {
    //        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
    //            @Override
    //            public boolean onQueryTextSubmit(String query) {
    //                if (isAdded()) {
    //                    textQuery = query;
    //                    mainViewModel.checkInternetSearch(requireContext());
    //                }
    //                return true;
    //            }
    //
    //            @Override
    //            public boolean onQueryTextChange(String newText) {
    //                if (newText == null) return true;
    //                if (newText.isEmpty()) {
    //                    binding.tvCurrent.setVisibility(View.VISIBLE);
    //                }
    //                binding.viewItemLocation.getRoot().setVisibility(View.GONE);
    //                binding.tvDone.setVisibility(View.GONE);
    //                binding.viewItemLocation.imDone.setVisibility(View.GONE);
    //                isShowMap = isSearch;
    //                return true;
    //            }
    //        });
    //    }
    //
    //    private void initListener() {
    //        backPress();
    //        binding.tvDone.setOnClickListener(v -> {
    //            ViewHelper.preventTwoClick(v);
    //            boolean isExit = false;
    //            if (listTurnOn.size() > 0) {
    //                for (ItemTurnOn item : listTurnOn) {
    //                    if (item.getNameLocation().equals(nameLocation)) {
    //                        isExit = true;
    //                        break;
    //                    }
    //                }
    //            }
    //            if (isExit) {
    //                toastText(R.string.location_already_exists);
    //            } else {
    //                App.tinyDB.putBoolean(Constant.AUTO_TIME_ON, false);
    //                App.tinyDB.putBoolean(Constant.AUTO_TIME_OFF, false);
    //                viewModel.updateLocationAutomation(focusIOS.getName(), nameLocation, address.getLatitude(), address.getLongitude(), System.currentTimeMillis(), itemTurnOnLocation.getNameLocation());
    //                mainViewModel.itemFocusDetail.postValue(focusIOS);
    //                ((MainActivity) requireActivity()).navControllerMain.popBackStack(
    //                        R.id.editlocationFragment,
    //                        true
    //                );
    //            }
    //
    //        });
    //        binding.viewItemLocation.getRoot().setOnClickListener(v -> {
    //            ViewHelper.preventTwoClick(v);
    //            MethodUtils.hideSoftKeyboard(requireActivity());
    //            binding.frameMap.setVisibility(View.VISIBLE);
    //            binding.tvDone.setVisibility(View.VISIBLE);
    //            binding.viewItemLocation.imDone.setVisibility(View.VISIBLE);
    //        });
    //        binding.tvCurrent.setOnClickListener(v -> {
    //            ViewHelper.preventTwoClick(v);
    //            binding.progressCircularApp.setVisibility(View.VISIBLE);
    //            if (ActivityCompat.checkSelfPermission(
    //                    requireContext(),
    //                    Manifest.permission.ACCESS_FINE_LOCATION
    //            ) != PackageManager.PERMISSION_GRANTED
    //                    && ActivityCompat.checkSelfPermission(
    //                    requireContext(),
    //                    Manifest.permission.ACCESS_COARSE_LOCATION
    //            )
    //                    != PackageManager.PERMISSION_GRANTED
    //            ) {
    //                return;
    //            }
    //            if (!MethodUtils.isGPSEnabled(requireContext())) {
    //                toastText(R.string.you_need_turn_on_location);
    //                binding.progressCircularApp.setVisibility(View.GONE);
    //            } else {
    //                mainViewModel.checkInternet(requireContext());
    //
    //            }
    //            MethodUtils.hideSoftKeyboard(requireActivity());
    //        });
    //    }
    //
    //    private void backPress() {
    //        binding.imBack.setOnClickListener(v -> {
    //            ViewHelper.preventTwoClick(v);
    //            MethodUtils.hideKeyboard(requireActivity());
    //            mainViewModel.itemFocusDetail.postValue(focusIOS);
    //            ((MainActivity) requireActivity()).navControllerMain.popBackStack(R.id.editlocationFragment, true);
    //        });
    //
    //    }
    //
    //    private void setupMap() {
    //        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
    //        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
    //        if (mapFragment != null) {
    //            mapFragment.getMapAsync(googleMap -> map = googleMap);
    //        }
    //    }
    //
    //    private void drawCircle(Double latitude, Double longitude) {
    //        CircleOptions circleOptions = new CircleOptions().center(new LatLng(latitude, longitude))
    //                .radius(500).strokeWidth(10.0f)
    //                .strokeColor(ContextCompat.getColor(requireContext(), R.color.color_007AFF))
    //                .fillColor(ContextCompat.getColor(requireContext(), R.color.color_1A007AFF));
    //        LatLng latLng = new LatLng(latitude, longitude);
    //        CameraPosition camPos = new CameraPosition.Builder().target(new LatLng(latitude, longitude))
    //                .zoom(14.0f)
    //                .build();
    //
    //        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
    //        if (map == null) {
    //            return;
    //        }
    //        if (marker != null) {
    //            marker.remove();
    //        }
    //        marker = map.addMarker(markerOptions);
    //        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f), 50000, null);
    //        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker));
    //        CameraUpdate camUpdate = CameraUpdateFactory.newCameraPosition(camPos);
    //        map.moveCamera(camUpdate);
    //        if (circle != null) {
    //            circle.remove();
    //        }
    //        circle = map.addCircle(circleOptions);
    //    }
    override fun onPermissionGranted() {}
    override fun onDestroy() {
        super.onDestroy()
        //        onBackPressedCallback.setEnabled(false);
    }
}