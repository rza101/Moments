<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use App\Models\SavedPost;
use Illuminate\Http\Request;
use Exception;

class SavedPostsController extends Controller
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
            $saved_post = SavedPost::firstOrNew(['username' => $request->username, 'post_id' => $request->post_id]);
            $saved_post->username = $request->username;
            $saved_post->post_id = $request->post_id;
            $saved_post->save();

            return response()->json([
                'status' => 200,
                'message' => 'Saved post stored'
            ]);
        } catch (Exception $e) {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to store saved post'
            ], 400);
        }
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show($username)
    {
        $saved_post = SavedPost::where('username', '=', $username)
        ->join('posts', 'saved_posts.post_id','=','posts.id')
        ->orderBy('created_at', 'DESC')
        ->get();

        if ($saved_post) {
            return response()->json([
                'status' => 200,
                'message' => 'Success',
                'data' => $saved_post
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to show saved post'
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
        $saved_post = SavedPost::find($id);

        if ($saved_post) {
            $saved_post->delete();

            return response()->json([
                'status' => 200,
                'message' => 'Saved post deleted'
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to delete saved post'
            ], 400);
        }
    }
}
