<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PostLike extends Model
{
    use HasFactory;

    protected $table = "post_likes";
    function Post(){
        return $this->belongsTo(Post::class, "post_id", "id");
    }
}
