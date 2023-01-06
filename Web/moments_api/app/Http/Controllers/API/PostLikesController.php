<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use App\Http\Controllers\FCMController;
use App\Models\PostLike;
use Illuminate\Http\Request;
use Exception;

class PostLikesController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        return response()->json([
            'status' => 200,
            'message' => 'Success'
        ]);
    }

    /**
     * Store a newly created resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @return \Illuminate\Http\Response
     */
    public function store(Request $request)
    {
        try {
            $post_like = PostLike::firstOrNew(['username' => $request->username, 'post_id' => $request->post_id]);
            $post_like->username = $request->username;
            $post_like->post_id = $request->post_id;
            $post_like->save();

            $fcmResult = FCMController::sendFCM($post_like->Post->User->fcm_token, "Post Like", "$post_like->username liked your post");

            return response()->json([
                'status' => 200,
                'message' => 'Post like stored',
                'data' => $fcmResult
            ]);
        } catch (Exception $e) {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to store post like'
            ], 400);
        }
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($post_id)
    {
        $post_like = PostLike::where('post_id', '=', $post_id)
            ->orderBy('post_likes.created_at', 'DESC')
            ->join('users', 'post_likes.username','=','users.username')
            ->select([
                'post_likes.*',
                'users.full_name AS full_name',
                'users.image_url AS image_url',
            ])
            ->get();

        if ($post_like) {
            return response()->json([
                'status' => 200,
                'message' => 'Success',
                'data' => $post_like
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to show post like'
            ], 400);
        }
    }

    /**
     * Update the specified resource in storage.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function update(Request $request, $id)
    {
        return response()->json([
            'status' => 200,
            'message' => 'Success'
        ]);
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        $post_like = PostLike::find($id);

        if ($post_like) {
            $post_like->delete();

            return response()->json([
                'status' => 200,
                'message' => 'Post like deleted'
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to delete post like'
            ], 400);
        }
    }
}
