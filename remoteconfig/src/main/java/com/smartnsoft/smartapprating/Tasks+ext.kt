package com.smartnsoft.smartapprating

import android.util.Log
import com.google.android.gms.tasks.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 *
 * @author Adrien Vitti
 * @since 2019.08.05
 */

suspend fun <TResult> Task<TResult>.awaitSuccess(): Boolean?
{
  return kotlin.coroutines.suspendCoroutine { continuation ->
    addOnCompleteListener { task ->
      if (task.isSuccessful)
      {
        continuation.resume(task.isSuccessful)
      }
      else
      {
        continuation.resumeWithException(task.exception ?: Exception("Unknown task exception"))
      }
    }
  }
}

suspend fun <TResult> Task<TResult>.awaitCompletion(): Boolean?
{
  Log.e("Ext", "awaitCompletion")
  return kotlin.coroutines.suspendCoroutine { continuation ->
    Log.e("Ext", "suspendCoroutine")
    addOnCanceledListener {
      Log.e("Ext", "addOnCanceledListener")
    }.addOnSuccessListener {
      Log.e("Ext", "addOnSuccessListener")
    }.addOnFailureListener {
      Log.e("Ext", "addOnFailureListener")
    }.addOnCompleteListener { task ->
      Log.e("Ext", "addOnCompleteListener")
      if (task.isComplete)
      {
        continuation.resume(task.isComplete)
      }
      else
      {
        continuation.resumeWithException(task.exception ?: Exception("Unknown task exception"))
      }
    }
  }
}
//
//suspend fun <TResult> Task<TResult>.await(): TResult?
//{
//  return kotlin.coroutines.suspendCoroutine { continuation ->
//    addOnCompleteListener { task ->
//      if (task.isSuccessful)
//      {
//        continuation.resume(task.result)
//      }
//      else
//      {
//        continuation.resumeWithException(task.exception ?: Exception("Unknown task exception"))
//      }
//    }
//  }
//}