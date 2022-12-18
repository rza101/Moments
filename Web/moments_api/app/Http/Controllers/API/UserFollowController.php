<?php

namespace App\Http\Controllers\API;

use App\Http\Controllers\Controller;
use App\Models\UserFollow;
use Exception;
use Illuminate\Http\Request;

class UserFollowController extends Controller
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
            $user_follow = new UserFollow();
            $user_follow->user_id = $request->user_id;
            $user_follow->user_following = $request->user_following;
            $user_follow->save();

            return response()->json([
                'status' => 200,
                'message' => 'User follow stored',
                'data' => $user_follow
            ]);
        } catch (Exception $e) {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to store user follow'
            ]);
        }
    }

    /**
     * Display the specified resource.
     *
     * @param  int  $id
     * @return \Illuminate\Http\Response
     */
    public function show(Request $request, $user_id)
    {
        if($request->type === 'follower'){
            $user_follow = UserFollow::where('user_id', '=', $user_id);
        }else{
            $user_follow = UserFollow::where('user_following', '=', $user_id);
        }

        if($user_follow){
            return response()->json([
                'status' => 200,
                'message' => 'Success',
                'data' => $user_follow
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to show user follow'
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
        $user_follow = UserFollow::find($id);

        if ($user_follow) {
            $user_follow->delete();
            return response()->json([
                'status' => 200,
                'message' => 'User follow deleted'
            ]);
        } else {
            return response()->json([
                'status' => 400,
                'message' => 'Failed to delete user follow'
            ]);
        }
    }
}
