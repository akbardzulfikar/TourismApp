package com.dicoding.tourismapp.core.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.tourismapp.core.data.source.remote.network.ApiResponse

import com.dicoding.tourismapp.core.utils.AppExecutors
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.flow.*

abstract class NetworkBoundResource<ResultType, RequestType> {

    private var result: Flow<Resource<ResultType>> = flow {
        emit(Resource.Loading())
        val dbSource = loadFromDB().first()
        if (shouldFetch(dbSource)) {
            emit(Resource.Loading())
            when (val apiResponse = createCall().first()) {
                is ApiResponse.Success -> {
                    saveCallResult(apiResponse.data)
                    emitAll(loadFromDB().map { Resource.Success(it) })
                }
                is ApiResponse.Empty -> {
                    emitAll(loadFromDB().map { Resource.Success(it) })
                }
                is ApiResponse.Error -> {
                    onFetchFailed()
                    emit(Resource.Error<ResultType>(apiResponse.errorMessage))
                }
            }
        } else {
            emitAll(loadFromDB().map { Resource.Success(it) })
        }
    }
    protected open fun onFetchFailed() {}

    protected abstract fun loadFromDB(): Flow<ResultType>

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract suspend fun createCall(): Flow<ApiResponse<RequestType>>

    protected abstract suspend fun saveCallResult(data: RequestType)

//    private fun fetchFromNetwork() {
//
//        val apiResponse = createCall()
//
//        result.onNext(Resource.Loading(null))
//        val response = apiResponse
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .take(1)
//            .doOnCancel {
//                mCompositeDisposable.dispose()
//            }
//            .subscribe { response ->
//                when (response) {
//                    is ApiResponse.Success -> {
//                        saveCallResult(response.data)
//                        val dbSource = loadFromDB()
//                        dbSource.subscribeOn(Schedulers.computation())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .take(1)
//                            .subscribe {
//                                dbSource.unsubscribeOn(Schedulers.io())
//                                result.onNext(Resource.Success(it))
//                            }
//                    }
//                    is ApiResponse.Empty -> {
//                        val dbSource = loadFromDB()
//                        dbSource.subscribeOn(Schedulers.computation())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .take(1)
//                            .subscribe {
//                                dbSource.unsubscribeOn(Schedulers.io())
//                                result.onNext(Resource.Success(it))
//                            }
//                    }
//                    is ApiResponse.Error -> {
//                        onFetchFailed()
//                        result.onNext(Resource.Error(response.errorMessage, null))
//                    }
//                }
//            }
//        mCompositeDisposable.add(response)
//    }

    fun asFlow(): Flow<Resource<ResultType>> = result
}