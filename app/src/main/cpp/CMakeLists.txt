cmake_minimum_required(VERSION 3.22.1)
project(dfnative)

add_library(dfnative SHARED dfnative.cpp)

find_library(log-lib log)

target_link_libraries(dfnative ${log-lib})

target_compile_options(dfnative PRIVATE
    -fvisibility=hidden
    -O2
    -fno-rtti
    -fno-exceptions
)
