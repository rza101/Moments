<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PostLikes;

class PostLikeController extends Controller
{
        /**
     * Display a listing of the resource.
     *
     * @return \Illuminate\Http\Response
     */
    public function index()
    {
        $PostLike = PostLikes::all();
        if($PostLike){
            return response()->json([
                'status' => 200,
                'message' => 'success',
                'data' => $PostLike
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
            $PostLike = new PostLikes();
            $PostLike->user_id = $request->user_id;
            $PostLike->post_id = $request->post_id;
            $PostLike->save();
            return response()->json([
                'status' => 200,
                'message' => 'success',
                'data' => $PostLike
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
        $PostLike = PostLikes::find($id);
        if ($PostLike) {
            return response()->json([
                'status' => 200,
                'message' => 'success',
                'data' => $PostLike
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
        $PostLike = PostLikes::find($id);
        if ($PostLike) {
            $PostLike->delete();
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
