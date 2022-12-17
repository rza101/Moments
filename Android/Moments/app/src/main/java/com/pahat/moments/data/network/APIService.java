package com.pahat.moments.data.network;

import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.data.network.model.PostComment;
import com.pahat.moments.data.network.model.PostLike;
import com.pahat.moments.data.network.model.SavedPost;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    // USER

    // USER FOLLOWERS

    // USER FOLLOWINGS

    // POSTS
    @GET("/posts")
    Call<APIResponse<List<Post>>> getAllPost();

    @GET("/posts/{user_id}")
    Call<APIResponse<List<Post>>> getAllPostByUserId(@Path("user_id") String user_id);

    @POST("/posts")
    @FormUrlEncoded
    Call<APIResponse<Post>> createPost(@Field("user_id") String user_id,
                                       @Field("image_url") String image_url,
                                       @Field("caption") String caption);

    @PUT("/posts/{post_id}")
    @FormUrlEncoded
    Call<APIResponse<Post>> updatePost(@Path("post_id") long post_id,
                                       @Field("caption") String caption);

    @DELETE("/posts/{post_id}")
    Call<APIResponse<Post>> deletePost(@Path("post_id") long post_id);

    // POST LIKE
    @GET("/postlikes")
    Call<APIResponse<List<PostLike>>> getAllPostLikes(@Query("post_id") long post_id);

    @POST("/postslikes")
    @FormUrlEncoded
    Call<APIResponse<Post>> createPostLike(@Field("post_id") long post_id,
                                           @Field("user_id") String user_id);

    @DELETE("/postslikes/{id}")
    @FormUrlEncoded
    Call<APIResponse<Post>> deletePostLike(@Path("post_id") long id);

    // POST COMMENTS
    @GET("/postcomments")
    Call<APIResponse<List<PostComment>>> getAllPostComments(@Query("post_id") long post_id);

    @POST("/postscomments")
    @FormUrlEncoded
    Call<APIResponse<Post>> createPostComment(@Field("post_id") long post_id,
                                              @Field("user_id") String user_id,
                                              @Field("comment") String comment);

    @DELETE("/postscomments/{id}")
    @FormUrlEncoded
    Call<APIResponse<Post>> deletePostComment(@Path("id") long id);

    // SAVED POSTS
    @GET("/savedposts")
    Call<APIResponse<List<SavedPost>>> getAllSavedPosts(@Query("post_id") long post_id);

    @POST("/savedposts")
    @FormUrlEncoded
    Call<APIResponse<Post>> createSavedPost(@Field("user_id") String user_id,
                                              @Field("post_id") long post_id);

    @DELETE("/savedposts/{id}")
    @FormUrlEncoded
    Call<APIResponse<Post>> deleteSavedPost(@Path("id") long id);
}
