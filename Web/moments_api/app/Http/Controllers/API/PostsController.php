<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use App\Models\Post;
use Illuminate\Http\Request;
use Exception;

class PostsController extends Controller
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
            'message' => 'Success',
            'data' => Post::all()
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
            $posts = new Post();
            $posts->user_id = $request->user_id;
            $posts->image_url = $request->image_url;
            $posts->caption = $request->caption;
            $posts->save();

            return response()->json([
                'status' => 200,
                'message' => 'Post stored',
                'data' => $posts
            ]);
        } catch (Exception $e) {
            return response(400)->json([
                'status' => 400,
                'message' => 'Failed to store post'
            ]);
        }
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($user_id)
    {
        $posts = Post::where('user_id', '=', $user_id)->get();
        
        if ($posts) {
            return response()->json([
                'status' => 200,
                'message' => 'Success',
                'data' => $posts
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to show post'
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
        try {
            $posts = Post::find($id);
            $posts->caption = $request->caption;
            if($posts){
                $posts->save();
                return response()->json([
                    'status' => 200,
                    'message' => 'post updated',
                    'data' => $posts
                ]);
            }
            else {
                return response(400)->json([
                    'status' => 400,
                    'message' => 'Failed to update post'
                ]);
            }
        } catch (Exception $e) {
            return response(400)->json([
                'status' => 400,
                'message' => 'Failed to update post'
            ]);
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
            return response(400)->json([
                'status' => 400,
                'message' => 'Failed to delete post'
            ]);
        }
    }
}
