<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use App\Http\Controllers\FCMController;
use Illuminate\Http\Request;
use App\Models\PostComment;
use Exception;

class PostCommentsController extends Controller
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
            'message' => 'success'
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
            $post_comment = new PostComment();
            $post_comment->username = $request->username;
            $post_comment->post_id = $request->post_id;
            $post_comment->comment = $request->comment;
            $post_comment->save();

            $fcmResult = FCMController::sendFCM($post_comment->Post->User->fcm_token, "Post Like", "$post_comment->username commented your post");

            return response()->json([
                'status' => 200,
                'message' => 'Post comment stored',
                'data' => $fcmResult
            ]);
        } catch (Exception $e) {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to store post comment'
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
        $post_comment = PostComment::where('post_id', '=', $post_id)
            ->orderBy('post_comments.created_at', 'DESC')
            ->join('users', 'post_comments.username', '=', 'users.username')
            ->select([
                'post_comments.*',
                'users.full_name AS full_name',
                'users.image_url AS image_url',
            ])
            ->get();

        if ($post_comment) {
            return response()->json([
                'status' => 200,
                'message' => 'Success',
                'data' => $post_comment
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to show post comment'
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
        $post_comment = PostComment::find($id);

        if ($post_comment) {
            $post_comment->delete();

            return response()->json([
                'status' => 200,
                'message' => 'Post comment deleted'
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to delete post comment'
            ], 400);
        }
    }
}
