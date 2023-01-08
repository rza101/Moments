<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use App\Models\Post;
use App\Models\PostComment;
use App\Models\PostLike;
use Illuminate\Http\Request;
use Exception;

class PostsController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index(Request $request)
    {
        if ($request->username) {
            return response()->json([
                'status' => 200,
                'message' => 'Success',
                'data' => Post::where('username', '=', $request->username)->orderBy('created_at', 'DESC')->get()
            ]);
        } else {
            return response()->json([
                'status' => 200,
                'message' => 'Success',
                'data' => Post::orderBy('created_at', 'DESC')->get()
            ]);
        }
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
            $posts = new Post();
            $posts->username = $request->username;
            $posts->image_url = $request->image_url;
            $posts->caption = $request->caption;
            $posts->save();

            return response()->json([
                'status' => 200,
                'message' => 'Post stored',
                'data' => $posts
            ]);
        } catch (Exception $e) {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to store post'
            ], 400);
        }
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($id)
    {
        $posts = Post::find($id);

        if ($posts) {
            $post_like = PostLike::where('post_likes.post_id', '=', $id)
                ->orderBy('post_likes.created_at', 'DESC')
                ->join('users', 'post_likes.username','=','users.username')
                ->select([
                    'post_likes.*',
                    'users.full_name AS full_name',
                    'users.image_url AS image_url',
                ])
                ->get();
            $post_comment = PostComment::where('post_comments.post_id', '=', $id)
                ->orderBy('post_comments.created_at', 'DESC')
                ->join('users', 'post_comments.username','=','users.username')
                ->select([
                    'post_comments.*',
                    'users.full_name AS full_name',
                    'users.image_url AS image_url',
                ])
                ->get();

            return response()->json([
                'status' => 200,
                'message' => 'Success',
                'data' => [
                    'post' => $posts,
                    'post_like' => $post_like,
                    'post_comment' => $post_comment
                ]
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to show post'
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
        try {
            $posts = Post::find($id);

            if ($posts) {
                $posts->caption = $request->caption;
                $posts->save();

                return response()->json([
                    'status' => 200,
                    'message' => 'post updated'
                ]);
            } else {
                return response()->json([
                    'status' => 400,
                    'message' => 'Failed to update post'
                ], 400);
            }
        } catch (Exception $e) {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to update post'
            ], 400);
        }
    }

    /**
     * Remove the specified resource from storage.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function destroy($id)
    {
        $posts = Post::find($id);
        if ($posts) {
            $posts->delete();

            return response()->json([
                'status' => 200,
                'message' => 'Post deleted'
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to delete post'
            ], 400);
        }
    }
}
