<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PostComment extends Model
{
    use HasFactory;

    protected $table = "post_comments";
    function Post(){
        return $this->belongsTo(Post::class, "post_id", "id");
    }
}
