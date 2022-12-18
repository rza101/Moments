<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
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
            $post_like = new PostLike();
            $post_like->user_id = $request->user_id;
            $post_like->post_id = $request->post_id;
            $post_like->save();

            return response()->json([
                'status' => 200,
                'message' => 'Post like stored',
                'data' => $post_like
            ]);
        } catch (Exception $e) {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to store post like'
            ]);
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
        $post_like = PostLike::where('post_id', '=', $post_id);

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
            ]);
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
            ]);
        }
    }
}
