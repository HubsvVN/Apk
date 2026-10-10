#include <jni.h>
#include <android/log.h>
#include <cstring>
#include <unistd.h>
#include <sys/ptrace.h>

#define TAG "dfn"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)

// simple anti-ptrace (common basic check)
static void anti_ptrace() {
    if (ptrace(PTRACE_TRACEME, 0, 0, 0) == -1) {
        // already traced – optional behaviour
    }
}

static bool g_esp = false;
static bool g_line = false;
static bool g_aim = false;
static bool g_recoil = false;

extern "C" JNIEXPORT void JNICALL
Java_com_dfx_core_NativeBridge_setFeature(JNIEnv *env, jclass, jstring jname, jboolean en) {
    const char *name = env->GetStringUTFChars(jname, nullptr);
    if (!name) return;

    if (strstr(name, "ESP Box"))   g_esp = en;
    else if (strstr(name, "ESP Line")) g_line = en;
    else if (strstr(name, "Aim"))  g_aim = en;
    else if (strstr(name, "Recoil")) g_recoil = en;

    // LOGI("feat %s -> %d", name, (int)en); // strip in release
    env->ReleaseStringUTFChars(jname, name);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_dfx_core_NativeBridge_isReady(JNIEnv *, jclass) {
    anti_ptrace();
    return JNI_TRUE;
}

// placeholder for future process_vm_readv when running under same-UID virtual space
// bool read_mem(pid_t pid, uintptr_t addr, void *buf, size_t len) { ... }
