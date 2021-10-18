#include "net_kodein_demo_crypto_Secp256k1Jni.h"
#include "secp256k1.h"

extern "C" {

    JNIEXPORT jlong JNICALL Java_net_kodein_demo_crypto_Secp256k1Jni_createContext_0002dMlkFRG8
      (JNIEnv *, jobject, jint type)
    {
        int flags;
        if (type == 0) flags = SECP256K1_CONTEXT_SIGN;
        else if (type == 1) flags = SECP256K1_CONTEXT_VERIFY;
        else return 0;
        return (jlong) secp256k1_context_create(flags);
    }

    JNIEXPORT void JNICALL Java_net_kodein_demo_crypto_Secp256k1Jni_destroyContext
      (JNIEnv *, jobject, jlong jctx)
    {
        secp256k1_context* ctx = (secp256k1_context *) jctx;
        secp256k1_context_destroy(ctx);
    }

    JNIEXPORT jboolean JNICALL Java_net_kodein_demo_crypto_Secp256k1Jni_verifySecKey
      (JNIEnv *env, jobject, jlong jctx, jbyteArray jseckey)
    {
        secp256k1_context* ctx = (secp256k1_context *) jctx;
        jbyte *seckey = env->GetByteArrayElements(jseckey, 0);
        int result = secp256k1_ec_seckey_verify((secp256k1_context *) ctx, (unsigned char*) seckey);
        env->ReleaseByteArrayElements(jseckey, seckey, 0);
        return result;
    }

    JNIEXPORT jbyteArray JNICALL Java_net_kodein_demo_crypto_Secp256k1Jni_createPubKey
      (JNIEnv *env, jobject, jlong jctx, jbyteArray jseckey)
    {
        secp256k1_context* ctx = (secp256k1_context *) jctx;
        jbyte *seckey = env->GetByteArrayElements(jseckey, 0);
        secp256k1_pubkey pub;
        int result = secp256k1_ec_pubkey_create(ctx, &pub, (unsigned char*) seckey);
        env->ReleaseByteArrayElements(jseckey, seckey, 0);
        if (result == 0) return NULL;
        jbyteArray jpubkey = env->NewByteArray(65);
        jbyte *pubkey = env->GetByteArrayElements(jpubkey, 0);
        size_t len = 65;
        result = secp256k1_ec_pubkey_serialize(ctx, (unsigned char*) pubkey, &len, &pub, SECP256K1_EC_UNCOMPRESSED);
        env->ReleaseByteArrayElements(jpubkey, pubkey, 0);
        if (result == 0) return NULL;
        return jpubkey;
        return NULL;
    }

    JNIEXPORT jbyteArray JNICALL Java_net_kodein_demo_crypto_Secp256k1Jni_signMessage
      (JNIEnv *env, jobject, jlong jctx, jbyteArray jmsg, jbyteArray jseckey)
    {
        secp256k1_context* ctx = (secp256k1_context *) jctx;
        jbyte *seckey = env->GetByteArrayElements(jseckey, 0);
        jbyte *msg = env->GetByteArrayElements(jmsg, 0);
        secp256k1_ecdsa_signature signature;
        int result = secp256k1_ecdsa_sign(ctx, &signature, (unsigned char*) msg, (unsigned char*) seckey, NULL, NULL);
        env->ReleaseByteArrayElements(jseckey, seckey, 0);
        env->ReleaseByteArrayElements(jmsg, msg, 0);
        if (result == 0) return NULL;
        jbyteArray jsig = env->NewByteArray(64);
        jbyte *sig = env->GetByteArrayElements(jsig, 0);
        result = secp256k1_ecdsa_signature_serialize_compact(ctx, (unsigned char*) sig, &signature);
        env->ReleaseByteArrayElements(jsig, sig, 0);
        if (result == 0) return NULL;
        return jsig;
    }

    JNIEXPORT jint JNICALL Java_net_kodein_demo_crypto_Secp256k1Jni_verifySignature
      (JNIEnv *env, jobject, jlong jctx, jbyteArray jsig, jbyteArray jmsg, jbyteArray jpubkey)
    {
        secp256k1_context* ctx = (secp256k1_context *)jctx;
        jbyte *sig = env->GetByteArrayElements(jsig, 0);
        secp256k1_ecdsa_signature signature;
        int result = secp256k1_ecdsa_signature_parse_compact(ctx, &signature, (unsigned char*) sig);
        env->ReleaseByteArrayElements(jsig, sig, 0);
        if (result == 0) return -1;
        size_t pubSize = env->GetArrayLength(jpubkey);
        jbyte *pubkeyBytes = env->GetByteArrayElements(jpubkey, 0);
        secp256k1_pubkey pubkey;
        result = secp256k1_ec_pubkey_parse(ctx, &pubkey, (unsigned char*) pubkeyBytes, pubSize);
        env->ReleaseByteArrayElements(jpubkey, pubkeyBytes, 0);
        if (result == 0) return - 1;
        jbyte *msg = env->GetByteArrayElements(jmsg, 0);
        result = secp256k1_ecdsa_verify(ctx, &signature, (unsigned char*) msg, &pubkey);
        env->ReleaseByteArrayElements(jmsg, msg, 0);
        return result > 0 ? 1 : 0;
    }

}
