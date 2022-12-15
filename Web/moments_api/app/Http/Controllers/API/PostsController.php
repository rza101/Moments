<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Posts;

class PostsController extends Controller
{
    /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $posts = Posts::all();
        if($posts){
            return response()->json([
                'status' => 200,
                'message' => 'success',
                'data' => $posts
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
            $posts = new Posts();
            $posts->user_id = $request->user_id;
            $posts->image_url = $request->image_url;
            $posts->caption = $request->caption;
            $posts->save();
            return response()->json([
                'status' => 200,
                'message' => 'success',
                'data' => $posts
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
        $posts = Posts::find($id);
        if ($posts) {
            return response()->json([
                'status' => 200,
                'message' => 'success',
                'data' => $posts
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'failed'
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
            $posts = Posts::find($id);
            $posts->user_id = $request->user_id;
            $posts->image_url = $request->image_url;
            $post->caption = $request->caption;
            $posts->save();
            return response()->json([
                'status' => 200,
                'message' => 'success',
                'data' => $posts
            ]);
        } catch (Exception $e) {
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
        $posts = Posts::find($id);
        if ($posts) {
            $posts->delete();
            return response()->json([
                'status' => 202,
                'message' => 'Data berhasil dihapus!'
            ]);
        } else {
            return response()->json([
                'status' => 204,
                'message' => 'Data gagal dihapus!'
            ]);
        }

    }
}
