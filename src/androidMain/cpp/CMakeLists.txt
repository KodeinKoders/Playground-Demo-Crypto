cmake_minimum_required(VERSION 3.10.0)

project("Secp256k1Jni")

add_library(
    secp256k1-jni
    SHARED
    secp256k1-jni.cpp
)

target_include_directories(
    secp256k1-jni
    PUBLIC ${CMAKE_CURRENT_LIST_DIR}/../../../secp256k1/secp256k1/include
    PUBLIC ${CMAKE_CURRENT_LIST_DIR}/../../../build/nativeHeaders
)

target_link_libraries( secp256k1-jni
    ${CMAKE_CURRENT_LIST_DIR}/../../../secp256k1/build/android/${ANDROID_ABI}/libsecp256k1.a
)