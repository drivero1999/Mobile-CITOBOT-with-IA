package com.yulagarces.citobot.data.services

import com.yulagarces.citobot.data.models.*
import com.yulagarces.citobot.data.responses.*
import com.yulagarces.citobot.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @GET(Constants.BASE_URL+"usuarios/email")
    fun getUserByEmail(@Query("email") email: String): Call<UserResponse>

    //Patiente
    @GET(Constants.BASE_URL+"pacientes/identificacion")
    fun getPatientById(@Query("id") id: String): Call<PatientResponse>

    //Screenings
    @GET(Constants.BASE_URL+"tamizajes/identificacion")
    fun getScreeningById(@Query("id") id: String): Call<ScreeningResponse>

    @POST(Constants.BASE_URL+"tamizajes/crear")
    fun createScreening(@Body screening: Tamizajes): Call<InsertScreeningResponse>

    @POST(Constants.BASE_URL+"imagenes/crear")
    fun createImage(@Body image: ImagesModel): Call<ImageResponse>

    @POST(Constants.BASE_URL+"imagenes/ftp")
    fun createFTP(@Body ftp: Ftp): Call<FtpResponse>
    //Person
    @Headers("Accept: application/json")
    @POST(Constants.BASE_URL+"personas/crear")
    fun createPerson(@Body person: Person): Call<PersonResponse>

    @Headers("Accept: application/json")
    @POST(Constants.BASE_URL+"pacientes/crear")
    fun createPatient(@Body patient: Patient): Call<PatientDtoResponse>

    @GET(Constants.BASE_URL+"imagenes/obtener")
    fun getImageByTamId(@Query("id") id: String): Call<SearchImageResponse>

    @GET(Constants.BASE_URL+"imagenes/get-img-aws"+"/{fileName}")
    fun getImageByName(@Path("fileName")  fileName: String): Call<ResponseBody>

    //eps
    @GET(Constants.BASE_URL+"eps/consultar")
    fun getEps(): Call<EpsResponse>

    @PUT(Constants.BASE_URL+"pacientes/actualizar/{id}")
    fun updatePatientById(@Path("id") id: String, @Body patient: Patient): Call<PatientDtoResponse>

    @PUT(Constants.BASE_URL+"personas/actualizar/{id}")
    fun updatePersonById(@Path("id") id: String, @Body person: PersonUpdate): Call<PersonUpdateResponse>

//    @Multipart
//    @POST("imagenes/save-img-aws")
//    fun uplooadFile(
//        @Part file: MultipartBody.Part,
//        @Part("nombre") nombre: RequestBody?
//    ): Observable<ResponseBody?>?


    @Multipart
    @POST("imagenes/save-img-aws")
    fun uploadImage(@Part file: MultipartBody.Part, @Part("nombre") nombre: RequestBody):Call<ResponseBody>
}