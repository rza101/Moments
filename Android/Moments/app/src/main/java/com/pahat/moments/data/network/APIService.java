package com.pahat.moments.data.network;

import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.APIUser;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.data.network.model.PostComment;
import com.pahat.moments.data.network.model.PostLike;
import com.pahat.moments.data.network.model.SavedPost;
import com.pahat.moments.data.network.model.UserFollow;
import com.pahat.moments.data.network.model.UserFollowComposite;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {
    // USER
    @GET("users/{user_id}")
    Call<APIResponse<List<APIUser>>> getUserByUID(@Path("user_id") String user_id);

    @POST("users")
    @FormUrlEncoded
    Call<APIResponse<List<APIUser>>> createUser(@Field("user_id") String user_id, @Field("fcm_token") String fcm_token);

    @PUT("users/{user_id}")
    @FormUrlEncoded
    Call<APIResponse<List<APIUser>>> updateUser(@Path("user_id") String user_id, @Field("fcm_token") String fcm_foken);

    // USER FOLLOW
    @GET("userfollow/{user_id}")
    Call<APIResponse<UserFollowComposite>> getUserFollow(@Path("user_id") String user_id);

    @POST("userfollow")
    Call<APIResponse<List<UserFollow>>> createUserFolloe(@Field("user_id") String user_id,
                                                         @Field("user_following") String user_following);

    @DELETE("userfollow/{user_foll_id}")
    Call<APIResponse<UserFollow>> deleteUserFollow(@Path("user_foll_id") long user_follow_id);

    // POSTS
    @GET("posts")
    Call<APIResponse<List<Post>>> getAllPost();

    @GET("posts/{user_id}")
    Call<APIResponse<List<Post>>> getAllPostByUserId(@Path("user_id") String user_id);

    @POST("posts")
    @FormUrlEncoded
    Call<APIResponse<Post>> createPost(@Field("user_id") String user_id,
                                       @Field("image_url") String image_url,
                                       @Field("caption") String caption);

    @PUT("posts/{post_id}")
    @FormUrlEncoded
    Call<APIResponse<Post>> updatePost(@Path("post_id") long post_id,
                                       @Field("caption") String caption);

    @DELETE("posts/{post_id}")
    Call<APIResponse<Post>> deletePost(@Path("post_id") long post_id);

    // POST LIKE
    @GET("postlikes/{post_id}")
    Call<APIResponse<List<PostLike>>> getAllPostLikes(@Path("post_id") long post_id);

    @POST("postlikes")
    @FormUrlEncoded
    Call<APIResponse<Post>> createPostLike(@Field("post_id") long post_id,
                                           @Field("user_id") String user_id);

    @DELETE("postlikes/{id}")
    @FormUrlEncoded
    Call<APIResponse<Post>> deletePostLike(@Path("post_id") long id);

    // POST COMMENTS
    @GET("postcomments/{post_id}")
    Call<APIResponse<List<PostComment>>> getAllPostComments(@Path("post_id") long post_id);

    @POST("postcomments")
    @FormUrlEncoded
    Call<APIResponse<Post>> createPostComment(@Field("post_id") long post_id,
                                              @Field("user_id") String user_id,
                                              @Field("comment") String comment);

    @DELETE("postcomments/{id}")
    @FormUrlEncoded
    Call<APIResponse<Post>> deletePostComment(@Path("id") long id);

    // SAVED POSTS
    @GET("savedposts/{user_id}")
    Call<APIResponse<List<SavedPost>>> getAllSavedPosts(@Path("user_id") long post_id);

    @POST("savedposts")
    @FormUrlEncoded
    Call<APIResponse<Post>> createSavedPost(@Field("user_id") String user_id,
                                            @Field("post_id") long post_id);

    @DELETE("savedposts/{id}")
    @FormUrlEncoded
    Call<APIResponse<Post>> deleteSavedPost(@Path("id") long id);
}
