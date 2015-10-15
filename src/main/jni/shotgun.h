/*
 * shotgun.h
 *
 *  Created on: Feb 13, 2013
 *      Author: Hank
 */

#include <jni.h>
#include <cstdio>
#include "common.h"

#ifndef SHOTGUN_H_
#define SHOTGUN_H_
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_edu_cmu_cs_lane_pipeline_dataanalyzer_ShotgunAnalyzer_clearMemory
	(JNIEnv *env, jobject obj);

JNIEXPORT void JNICALL Java_edu_cmu_cs_lane_pipeline_dataanalyzer_ShotgunAnalyzer_readMatrix
	(JNIEnv *env, jobject obj, jobjectArray x, jint num_rows, jint num_cols);

JNIEXPORT void JNICALL Java_edu_cmu_cs_lane_pipeline_dataanalyzer_ShotgunAnalyzer_readVector
	(JNIEnv *env, jobject obj, jdoubleArray y);

JNIEXPORT void JNICALL Java_edu_cmu_cs_lane_pipeline_dataanalyzer_ShotgunAnalyzer_lasso
	(JNIEnv *env, jobject obj, jdouble lambda, jint K, jdouble threshold, jint maxiter, jint verbose);

JNIEXPORT void JNICALL Java_edu_cmu_cs_lane_pipeline_dataanalyzer_ShotgunAnalyzer_logreg
	(JNIEnv *env, jobject obj, jdouble lambda, jdouble threshold, jint maxiter, jint verbose);

JNIEXPORT jdoubleArray JNICALL Java_edu_cmu_cs_lane_pipeline_dataanalyzer_ShotgunAnalyzer_getResults
	(JNIEnv *env, jobject obj);

#ifdef __cplusplus
}
#endif
#endif /* SHOTGUN_H_ */
