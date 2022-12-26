package com.pahat.moments.data.network;

import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.APIUser;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.data.network.model.PostComment;
import com.pahat.moments.data.network.model.PostComposite;
import com.pahat.moments.data.network.model.PostLike;
import com.pahat.moments.data.network.model.SavedPost;
import com.pahat.moments.data.network.model.Sender;
import com.pahat.moments.data.network.model.UserFollow;
import com.pahat.moments.data.network.model.UserFollowComposite;
import com.pahat.moments.data.network.model.ViewData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {
    // USER
    @GET("users/{user_id}")
    Call<APIResponse<APIUser>> getUserByUID(@Path("user_id") String user_id);

    @POST("users")
    @FormUrlEncoded
    Call<APIResponse<APIUser>> createUser(@Field("user_id") String user_id,
                                                @Field("username") String username,
                                                @Field("fcm_token") String fcm_token);

    @POST("users/{user_id}/update")
    @FormUrlEncoded
    Call<APIResponse<APIUser>> updateUser(@Path("user_id") String user_id, @Field("fcm_token") String fcm_foken);

    // USER FOLLOW
    @GET("userfollow/{username}")
    Call<APIResponse<UserFollowComposite>> getUserFollow(@Path("username") String username);

    @POST("userfollow")
    Call<APIResponse<UserFollow>> createUserFolloe(@Field("username") String username,
                                                         @Field("username_following") String username_following);

    @POST("userfollow/{user_foll_id}/delete")
    Call<APIResponse<UserFollow>> deleteUserFollow(@Path("user_foll_id") long user_follow_id);

    // POSTS
    @GET("posts")
    Call<APIResponse<List<Post>>> getAllPost();

    @GET("posts")
    Call<APIResponse<List<Post>>> getAllPostByUserId(@Query("username") String username);

    @GET("posts/{id}")
    Call<APIResponse<PostComposite>> getPostById(@Path("id") String id);

    @POST("posts")
    @FormUrlEncoded
    Call<APIResponse<Post>> createPost(@Field("username") String username,
                                       @Field("image_url") String image_url,
                                       @Field("caption") String caption);

    @POST("posts/{post_id}/update")
    @FormUrlEncoded
    Call<APIResponse<Post>> updatePost(@Path("post_id") long post_id,
                                       @Field("caption") String caption);

    @POST("posts/{post_id}/delete")
    Call<APIResponse<Post>> deletePost(@Path("post_id") long post_id);

    // POST LIKE
    @GET("postlikes/{post_id}")
    Call<APIResponse<List<PostLike>>> getAllPostLikes(@Path("post_id") long post_id);

    @POST("postlikes")
    @FormUrlEncoded
    Call<APIResponse<PostLike>> createPostLike(@Field("post_id") long post_id,
                                           @Field("username") String username);

    @POST("postlikes/{post_like_id}/delete")
    @FormUrlEncoded
    Call<APIResponse<PostLike>> deletePostLike(@Path("post_like_id") long post_like_id);

    // POST COMMENTS
    @GET("postcomments/{post_id}")
    Call<APIResponse<List<PostComment>>> getAllPostComments(@Path("post_id") long post_id);

    @POST("postcomments")
    @FormUrlEncoded
    Call<APIResponse<PostComment>> createPostComment(@Field("post_id") long post_id,
                                              @Field("username") String username,
                                              @Field("comment") String comment);

    @POST("postcomments/{post_comment_id}/delete")
    @FormUrlEncoded
    Call<APIResponse<PostComment>> deletePostComment(@Path("post_comment_id") long post_comment_id);

    // SAVED POSTS
    @GET("savedposts/{username}")
    Call<APIResponse<List<SavedPost>>> getAllSavedPosts(@Path("username") long post_id);

    @POST("savedposts")
    @FormUrlEncoded
    Call<APIResponse<Post>> createSavedPost(@Field("username") String username,
                                            @Field("post_id") long post_id);

    @POST("savedposts/{saved_post_id}/delete")
    @FormUrlEncoded
    Call<APIResponse<Post>> deleteSavedPost(@Path("saved_post_id") long saved_post_id);

    @POST("fcm/send")
    Call<ViewData> sendNotification(@Body Sender body);
}
