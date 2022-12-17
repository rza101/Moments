<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\SavedPosts;

class SavedPostsController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $saved_posts = SavedPosts::all();
        if($saved_posts){
            return response()->json([
                'status' => 200,
                'message' => 'success',
                'data' => $saved_posts
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'failed'
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
            $saved_posts = new SavedPosts();
            $saved_posts->user_id = $request->user_id;
            $saved_posts->post_id = $request->post_id;
            $saved_posts->save();
            return response()->json([
                'status' => 200,
                'message' => 'success',
                'data' => $saved_posts
            ]);
        } catch (Exception $e) {
            return response()->json([
                'status' => 400,
                'message' => 'failed'
            ]);
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
        $saved_posts = SavedPosts::find($id);
        if ($saved_posts) {
            return response()->json([
                'status' => 200,
                'message' => 'success',
                'data' => $saved_posts
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'failed'
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
        $saved_posts = SavedPosts::find($id);
        if ($saved_posts) {
            $saved_posts->delete();
            return response()->json([
                'status' => 200,
                'message' => 'Data berhasil dihapus!'
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Data gagal dihapus!'
            ]);
        }

    }
}
