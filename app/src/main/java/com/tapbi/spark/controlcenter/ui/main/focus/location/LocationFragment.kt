package com.tapbi.spark.controlcenter.ui.main.focus.location

import android.os.Bundle
import android.view.View
import com.tapbi.spark.controlcenter.R
import com.tapbi.spark.controlcenter.databinding.FragmentLocationBinding
import com.tapbi.spark.controlcenter.ui.base.BaseBindingFragment

//import static android.Manifest.permission.ACCESS_FINE_LOCATION;
class LocationFragment : BaseBindingFragment<FragmentLocationBinding, LocationViewModel>() {
    //    private FusedLocationProviderClient fusedLocationProviderClient;
    //    private FocusIOS focusIOS;
    //    private GoogleMap map;
    //    private String textQuery = "";
    //    private Address address;
    //    private Marker marker;
    //    private Circle circle;
    //    private String nameLocation = "";
    //    private List<ItemTurnOn> listTurnOn = new ArrayList<>();
    //    private AlertDialog dialogPermissionAlertDialog;
    //    private final ActivityResultLauncher<String> requestPermissionLauncherLocation = registerForActivityResult(
    //            new ActivityResultContracts.RequestPermission(), result -> {
    //                if (result) {
    //                    if (!MethodUtils.isGPSEnabled(requireContext())) {
    //                        toastText(R.string.you_need_turn_on_location);
    //                        binding.progressCircularApp.setVisibility(View.GONE);
    //                    } else {
    //                        mainViewModel.checkInternet(requireContext());
    //                    }
    //                } else {
    //                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
    //                        boolean b = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);
    //                        if (getContext() != null && !b) {
    //                            if (dialogPermissionAlertDialog == null) {
    //                                dialogPermissionAlertDialog = MethodUtils.showDialogPermission(getContext(), true, false);
    //                            }
    //                            if (!dialogPermissionAlertDialog.isShowing()) {
    //                                dialogPermissionAlertDialog.show();
    //                            }
    //                        } else {
    //                            toastText(R.string.text_detail_when_permission_location);
    //                        }
    //                    } else {
    //                        toastText(R.string.text_detail_when_permission_location);
    //                    }
    //                }
    //            });
    override fun getViewModel(): Class<LocationViewModel> {
        return LocationViewModel::class.java
    }

    override val layoutId: Int
        //    @Override
        get() = R.layout.fragment_location

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    }

    override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
//        if (savedInstanceState != null) {
//            viewModel.getFocusFromName(savedInstanceState.getString(Constant.KEY_SAVE_FOCUS));
//        }
//
//        initView();
//        observerData();
    }

    //    private void observerData() {
    //        mainViewModel.itemFocusNewAutomationLocation.observe(getViewLifecycleOwner(), focusIOS -> {
    //            if (focusIOS != null) {
    //                this.focusIOS = focusIOS;
    //                mainViewModel.getListAutomationByFocus(focusIOS.getName());
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
    //                    binding.frameMap.setVisibility(View.VISIBLE);
    //                    if (!textQuery.isEmpty()) {
    //                        viewModel.getMap(requireContext(), textQuery);
    //                        binding.tvCurrent.setVisibility(View.GONE);
    ////                        if (isShowMap) {
    ////                            binding.frameMap.visibility = View.VISIBLE
    ////                            binding.viewItemLocation.imDone.visibility = View.VISIBLE
    ////                        }
    //                    } else {
    //                        binding.viewItemLocation.getRoot().setVisibility(View.GONE);
    //                        binding.tvDone.setVisibility(View.GONE);
    //                    }
    //                } else {
    //                    toastText(R.string.error_internet);
    //
    //                }
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
    //                binding.frameMap.setVisibility(View.GONE);
    //                drawCircle(addresses.getLatitude(), addresses.getLongitude());
    ////                    if (isShowMap) {
    ////                        Utils.hideKeyboard(requireActivity());
    ////                        binding.frameMap.visibility = View.VISIBLE
    ////                        binding.viewItemLocation.imDone.visibility = View.VISIBLE
    ////                        binding.tvDone.visibility = View.VISIBLE
    ////                    }
    ////                    isSearch = false
    //            } else {
    //                toastText(R.string.error_no_found);
    //                binding.frameMap.setVisibility(View.VISIBLE);
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
    //        viewModel.saveFocus.observe(getViewLifecycleOwner(), focusIOS -> {
    //            if (focusIOS != null) {
    //                this.focusIOS = focusIOS;
    //                mainViewModel.getListAutomationByFocus(focusIOS.getName());
    //            }
    //        });
    //    }
    //
    //    private void initView() {
    //        setUpPaddingStatusBar(binding.layoutLocation);
    //        setupMap();
    //        searchView();
    //        initListener();
    //        ((MainActivity) requireActivity()).setColorNavigation(R.color.color_F2F2F6);
    //    }
    //
    //    private void initListener() {
    //        backPress();
    //        binding.viewItemLocation.getRoot().setOnClickListener(v -> {
    //            ViewHelper.preventTwoClick(v);
    //            MethodUtils.hideSoftKeyboard(requireActivity());
    //            binding.frameMap.setVisibility(View.VISIBLE);
    //            binding.tvDone.setVisibility(View.VISIBLE);
    //            binding.viewItemLocation.imDone.setVisibility(View.VISIBLE);
    //
    //        });
    //        binding.tvDone.setOnClickListener(v -> {
    //            ViewHelper.preventTwoClick(v);
    //            boolean isExit = false;
    //            for (ItemTurnOn item : listTurnOn
    //            ) {
    //                if (item.getNameLocation().equals(nameLocation)) {
    //                    isExit = true;
    //                }
    //            }
    //            if (isExit) {
    //                toastText(R.string.location_already_exists);
    //            } else {
    //                App.tinyDB.putBoolean(Constant.AUTO_TIME_ON, false);
    //                App.tinyDB.putBoolean(Constant.AUTO_TIME_OFF, false);
    //                viewModel.insertAutomationFocus(new ItemTurnOn(focusIOS.getName(), true, false, -1, -1,
    //                        false, false, false, false, false, false, false, nameLocation,
    //                        address.getLatitude(), address.getLongitude(), "", "", Constant.LOCATION, System.currentTimeMillis()));
    //                mainViewModel.itemFocusDetail.postValue(focusIOS);
    ////                App.ins.setIsResetLocation(true);
    //                ((MainActivity) requireActivity()).navControllerMain.navigate(R.id.action_locationFragment_to_focusDetailFragment);
    //            }
    //        });
    //        binding.tvCurrent.setOnClickListener(v -> {
    //            ViewHelper.preventTwoClick(v);
    //            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
    //                    != PackageManager.PERMISSION_GRANTED
    //                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
    //                    != PackageManager.PERMISSION_GRANTED) {
    //                requestPermissionLauncherLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    //            }else {
    //                binding.progressCircularApp.setVisibility(View.VISIBLE);
    //                if (!MethodUtils.isGPSEnabled(requireContext())) {
    //                    toastText(R.string.you_need_turn_on_location);
    //                    binding.progressCircularApp.setVisibility(View.GONE);
    //                } else {
    //                    mainViewModel.checkInternet(requireContext());
    //                }
    //            }
    //            MethodUtils.hideSoftKeyboard(requireActivity());
    //        });
    //    }
    //
    //    private void backPress() {
    //        binding.imBack.setOnClickListener(v -> {
    //            ViewHelper.preventTwoClick(v);
    //            requireActivity().onBackPressed();
    //        });
    //
    //    }
    //
    //    OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
    //        @Override
    //        public void handleOnBackPressed() {
    //            ((MainActivity) requireActivity()).navControllerMain.popBackStack(R.id.locationFragment, true);
    //        }
    //    };
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
    //                if (newText.isEmpty()) {
    //                    binding.tvCurrent.setVisibility(View.VISIBLE);
    //                }
    ////                if (!isSearch) {
    ////                    listSearchLocation.clear()
    //                binding.viewItemLocation.getRoot().setVisibility(View.GONE);
    //                binding.tvDone.setVisibility(View.GONE);
    //                binding.viewItemLocation.imDone.setVisibility(View.GONE);
    ////                    isShowMap = false,
    ////                }
    //                return true;
    //            }
    //        });
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