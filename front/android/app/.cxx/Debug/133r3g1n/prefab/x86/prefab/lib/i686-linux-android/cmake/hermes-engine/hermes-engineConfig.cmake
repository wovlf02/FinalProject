if(NOT TARGET hermes-engine::libhermes)
add_library(hermes-engine::libhermes SHARED IMPORTED)
set_target_properties(hermes-engine::libhermes PROPERTIES
    IMPORTED_LOCATION "C:/Users/user/.gradle/caches/8.12.1/transforms/5dc14df0f7610ebd19cc671373d38b04/transformed/hermes-android-0.76.6-debug/prefab/modules/libhermes/libs/android.x86/libhermes.so"
    INTERFACE_INCLUDE_DIRECTORIES "C:/Users/user/.gradle/caches/8.12.1/transforms/5dc14df0f7610ebd19cc671373d38b04/transformed/hermes-android-0.76.6-debug/prefab/modules/libhermes/include"
    INTERFACE_LINK_LIBRARIES ""
)
endif()

