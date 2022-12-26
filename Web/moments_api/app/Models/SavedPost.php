<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class SavedPost extends Model
{
    use HasFactory;

    protected $table = "saved_posts";
    function Post(){
        return $this->belongsTo(Post::class, "post_id", "id");
    }
}
