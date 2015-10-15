/*
 * shotgun.c
 *
 *  Created on: Feb 13, 2013
 *      Author: Hank
 */

#include "shotgun.h"
#include <cstdio>
shotgun_data prob;

JNIEXPORT void JNICALL Java_edu_cmu_cs_lane_pipeline_dataanalyzer_ShotgunAnalyzer_clearMemory(JNIEnv *env, jobject obj) {

	prob.y.erase(prob.y.begin(),prob.y.end());
	prob.x.erase(prob.x.begin(),prob.x.end());
	prob.feature_consts.erase(prob.feature_consts.begin(),prob.feature_consts.end());

	prob.y.clear();
	prob.x.clear();
	prob.feature_consts.clear();

	std::vector<valuetype_t>().swap(prob.y);
	std::vector<valuetype_t>().swap(prob.x);

	//prob.A_cols.erase(prob.A_cols.begin(),prob.A_cols.end());
	//prob.A_rows.erase(prob.A_rows.begin(),prob.A_rows.end());

	// clear vectors
	prob.A_cols.clear();
	prob.A_rows.clear();

	// free memory
	std::vector<sparse_array>().swap(prob.A_cols);
	std::vector<sparse_array>().swap(prob.A_rows);

	std::vector<feature>().swap(prob.feature_consts);

}

JNIEXPORT void JNICALL Java_edu_cmu_cs_lane_pipeline_dataanalyzer_ShotgunAnalyzer_readMatrix(JNIEnv *env, jobject obj,
		jobjectArray x, jint num_rows, jint num_cols) {

	int num_vals = env->GetArrayLength(x);

	// resize vectors
	prob.A_cols.resize(num_cols);
	prob.A_rows.resize(num_rows);

	for (int i = 0; i < num_vals; i++) {
		jdoubleArray row = (jdoubleArray) env->GetObjectArrayElement(x, i);
		jdouble *col = env->GetDoubleArrayElements(row, 0);

		// read row
		int I = col[0];
		int J = col[1];
		double val = col[2];
		I--;
		J--;

		// store row
		prob.A_cols[J].add(I, val);
		prob.A_rows[I].add(J, val);

		env->ReleaseDoubleArrayElements(row, col, 0);
		env->DeleteLocalRef(row);
	}
	prob.nx = num_cols;
	prob.ny = num_rows;

}

JNIEXPORT void JNICALL Java_edu_cmu_cs_lane_pipeline_dataanalyzer_ShotgunAnalyzer_readVector(JNIEnv *env, jobject obj,
		jdoubleArray y) {

	jsize len = env->GetArrayLength(y);
	jdouble *vals = env->GetDoubleArrayElements(y, 0);

	prob.y.reserve(len);

	for (int i = 0; i < len; i++) {
		prob.y.push_back(vals[i]);
	}

	env->ReleaseDoubleArrayElements(y, vals, 0);
}

JNIEXPORT void JNICALL Java_edu_cmu_cs_lane_pipeline_dataanalyzer_ShotgunAnalyzer_lasso(JNIEnv *env, jobject obj,
		jdouble lambda, jint K, jdouble threshold, jint maxiter, jint verbose) {
	if (verbose==1)
		printf("-- Running Lasso Algorithm --\n");
	omp_set_num_threads(2);
	solveLasso(&prob, lambda, K, threshold, maxiter, verbose);
}

JNIEXPORT void JNICALL Java_edu_cmu_cs_lane_pipeline_dataanalyzer_ShotgunAnalyzer_logreg(JNIEnv *env, jobject obj,
		jdouble lambda, jdouble threshold, jint maxiter, jint verbose) {
	if (verbose==1)
		printf("-- Running Logarithmic Regression --\n");
	omp_set_num_threads(2);
	bool all_zero = false;
	compute_logreg(&prob, lambda, threshold, maxiter, verbose, all_zero);
}

JNIEXPORT jdoubleArray JNICALL Java_edu_cmu_cs_lane_pipeline_dataanalyzer_ShotgunAnalyzer_getResults(JNIEnv *env, jobject obj) {
	jdoubleArray results = env->NewDoubleArray(prob.nx);

	if (results) {
		jdouble temp[prob.nx];
		for (int i=0; i < prob.nx; i++) {
			temp[i] = prob.x[i];
		}
		env->SetDoubleArrayRegion(results, 0, prob.nx, temp);
	}

	return results;
}
