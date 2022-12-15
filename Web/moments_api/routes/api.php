<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\API\PostsController;
use App\Http\Controllers\API\PostLikeController;
use App\Http\Controllers\API\PostCommentsController;
use App\Http\Controllers\API\SavedPostsController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});
// ex: api/posts -> Create, Read
// ex: api/posts{id} -> Update, Show, Delete
Route::apiResource('posts', PostsController::class);
Route::apiResource('postlikes', PostLikeController::class);
Route::apiResource('postcomments', PostCommentsController::class);
Route::apiResource('savedposts', SavedPostsController::class);