<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\PostComments;

class PostCommentsController extends Controller
{   
    /**
    * Display a listing of the resource.
    *
    * @return \Illuminate\Http\Response
    */
   public function index()
   {
       $post_comments = PostComments::all();
       if($post_comments){
           return response()->json([
               'status' => 200,
               'message' => 'success',
               'data' => $post_comments
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
           $post_comments = new PostComments();
           $post_comments->user_id = $request->user_id;
           $post_comments->post_id = $request->post_id;
           $post_comments->comment = $request->comment;
           $post_comments->save();
           return response()->json([
               'status' => 201,
               'message' => 'success',
               'data' => $post_comments
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
       $post_comments = PostComments::find($id);
       if ($post_comments) {
           return response()->json([
               'status' => 200,
               'message' => 'success',
               'data' => $post_comments
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
       $post_comments = PostComments::find($id);
       if ($post_comments) {
           $post_comments->delete();
           return response()->json([
               'status' => 202,
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
