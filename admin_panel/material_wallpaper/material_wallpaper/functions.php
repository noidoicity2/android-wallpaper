<?php

   function insert($table, $data) {
        include 'includes/config.php';
        $fields = array_keys( $data );  
        $values = array_map(array($connect, 'real_escape_string'), array_values($data) );
        
        $sql = "INSERT INTO $table (".implode(",",$fields).") VALUES ('".implode("','", $values )."')";
        mysqli_query($connect, $sql);
    }

    function delete($table_name, $where_clause = '') {
        include 'includes/config.php';
        $whereSQL = '';
        if(!empty($where_clause)) {
            if(substr(strtoupper(trim($where_clause)), 0, 5) != 'WHERE') {
                $whereSQL = " WHERE ".$where_clause;
            } else {
                $whereSQL = " ".trim($where_clause);
            }
        }
        $sql = "DELETE FROM ".$table_name.$whereSQL;
        return mysqli_query($connect, $sql);

    }

    // Update Data, Where clause is left optional
    function update($table_name, $form_data, $where_clause = '') {

        include 'includes/config.php';
        // check for optional where clause
        $whereSQL = '';
        if(!empty($where_clause)) {
            // check to see if the 'where' keyword exists
            if(substr(strtoupper(trim($where_clause)), 0, 5) != 'WHERE') {
                // not found, add key word
                $whereSQL = " WHERE ".$where_clause;
            } else {
                $whereSQL = " ".trim($where_clause);
            }
        }
        // start the actual SQL statement
        $sql = "UPDATE ".$table_name." SET ";

        // loop and build the column /
        $sets = array();
        foreach($form_data as $column => $value) {
             $sets[] = "`".$column."` = '".$value."'";
        }
        $sql .= implode(', ', $sets);

        // append the where statement
        $sql .= $whereSQL;
             
        // run and return the query result
        return mysqli_query($connect, $sql);
    }

    function clean($data) {
        include 'includes/config.php';
        $data = mysqli_real_escape_string($connect, $data);
        return $data; 
    }

    function encrypt($key) {
        $key = base64_encode(base64_encode(base64_encode($key)));
        return $key; 
    }

    function FCM($uniqueId, $title, $message, $bigImage, $link, $postId, $fcmServerKey, $fcmNotificationTopic, $redirect) {

        $data = array(
            'to' => '/topics/' . $fcmNotificationTopic,
            'data' => array(
                'title' => $title,
                'message' => $message,
                'big_image' => $bigImage,
                'link' => $link,
                'post_id' => $postId,
                "unique_id"=> $uniqueId
            )
        );

        $header = array(
            'Authorization: key=' . $fcmServerKey,
            'Content-Type: application/json'
        );

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $header);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, TRUE);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        $result = curl_exec($ch);

        if (curl_errno($ch)) {
            echo json_encode(false);
        } else {
            echo json_encode(true);
        }

        curl_close($ch);

        $_SESSION['msg'] = "FCM push notification sent...";
        header($redirect);
        //header('Location:index.php');
        exit; 

    }

    function ONESIGNAL($uniqueId, $title, $message, $bigImage, $link, $postId, $oneSignalAppId, $oneSignalRestApiKey, $redirect) {

        $content = array("en" =>  $message);

        $fields = array(
            'app_id' => $oneSignalAppId,
            'included_segments' => array('All'),                                            
            'data' => array(
                "foo" => "bar",
                "link" => $link,
                "post_id" => $postId,
                "unique_id" => $uniqueId
            ),
            'headings'=> array("en" => $title),
            'contents' => $content,
            'big_picture' => $bigImage         
        );

        $fields = json_encode($fields);
        print("\nJSON sent:\n");
        print($fields);

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, "https://onesignal.com/api/v1/notifications");
        curl_setopt($ch, CURLOPT_HTTPHEADER, array('Content-Type: application/json; charset=utf-8',
            'Authorization: Basic '. $oneSignalRestApiKey));
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
        curl_setopt($ch, CURLOPT_HEADER, FALSE);
        curl_setopt($ch, CURLOPT_POST, TRUE);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $fields);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, FALSE);

        $response = curl_exec($ch);
        curl_close($ch);

        $_SESSION['msg'] = "OneSignal push notification sent...";
        header($redirect);
        exit;       

    }

    function pagination($reload, $page, $keyword, $tpages) {

        $prevlabelDisabled  = "<div class='padding-pagination button-grey button-rounded'><span aria-hidden='true'>&larr;</span>&nbsp;&nbsp;Previous</div>";
        $prevlabel  = "<div class='padding-pagination button button-rounded waves-effect waves-float'><span aria-hidden='true'>&larr;</span>&nbsp;&nbsp;Previous</div>";
        
        $nextlabel  = "<div class='padding-pagination button button-rounded waves-effect waves-float'>Next&nbsp;&nbsp;<span aria-hidden='true'>&rarr;</span></div>";
        $nextlabelDisabled  = "<div class='padding-pagination button-grey button-rounded'>Next&nbsp;&nbsp;<span aria-hidden='true'>&rarr;</span></div>";
        
        $current = "<div class='padding-pagination'>Page $page of $tpages</div>";
        
        $out = "<ul class='pager'>";
        
        // previous
        if($page == 1) {
            $out.= "<li class='previous disabled'><a>".$prevlabelDisabled."</a></li>";
        }
        elseif($page == 2) {
            $out.= "<li class='previous'><a href='".$reload."'>".$prevlabel."</a></li>";
        } else {
            $out.= "<li class='previous'><a href='".$reload."?page=".($page-1)."&keyword=".$keyword."'>".$prevlabel."</a></li>";
        }
        
        // current
        $out.= "<li><a>".$current."</a></li>";
        
        // next
        if($page<$tpages) {
            $out.= "<li class='next'><a href='" . $reload . "?page=" .($page+1) . "&keyword=".$keyword." '>" . $nextlabel . "</a></li>";
        } else {
            $out.= "<li class='next disabled'><a>" . $nextlabelDisabled . "</a></li>";
        }
        
        $out.= "</ul>";
        
        return $out;
    }

    function remotefileSize($url) {
        //return byte
        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_NOBODY, 1);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER,false);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 0);
        curl_setopt($ch, CURLOPT_HEADER, 0);
        curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
        curl_setopt($ch, CURLOPT_MAXREDIRS, 3);
        curl_exec($ch);
        $filesize = curl_getinfo($ch, CURLINFO_CONTENT_LENGTH_DOWNLOAD);
        curl_close($ch);
        if ($filesize) return $filesize;
    }

    function getDriveFileId($url) {
        preg_match('~/d/\K[^/]+(?=/)~', $url, $result);
        return $result[0];
    }

    function compress_image($source_url, $destination_url, $quality) {

        $info = getimagesize($source_url);

            if ($info['mime'] == 'image/jpeg')
                  $image = imagecreatefromjpeg($source_url);

            elseif ($info['mime'] == 'image/gif')
                  $image = imagecreatefromgif($source_url);

          elseif ($info['mime'] == 'image/png')
                  $image = imagecreatefrompng($source_url);

            imagejpeg($image, $destination_url, $quality);
        return $destination_url;
    }    

    function create_thumbnail($target_folder ='', $thumb_folder = '', $file_ext = '') {

         $target_path = $target_folder;
         $thumb_path = $thumb_folder;  

         $thumbnail = $thumb_path;
         $upload_image = $target_path;

         list($width, $height) = getimagesize($upload_image);

         $thumb_width = 300;
         $thumb_height = floor(($thumb_width/$width)*$height);

         switch($file_ext) {
            case 'jpg':
                $source = imagecreatefromjpeg($upload_image);
            break;

            case 'jpeg':
                $source = imagecreatefromjpeg($upload_image);
            break;

            case 'png':
                $source = imagecreatefrompng($upload_image);
            break;

            case 'gif':
                $source = imagecreatefromgif($upload_image);
            break;

            default:
                $source = imagecreatefromjpeg($upload_image);
        }

         $thumb_create = imagecreatetruecolor($thumb_width, $thumb_height);
         switch($file_ext) {
            case 'png':
                imagealphablending($thumb_create, false);
                imagesavealpha($thumb_create, true);
            break;

            default:
            //none
        }

        imagecopyresized($thumb_create, $source, 0, 0, 0, 0, $thumb_width, $thumb_height, $width, $height);
        switch($file_ext) {
            case 'jpg' || 'jpeg':
                imagejpeg($thumb_create, $thumbnail, 80);
            break;

            case 'png':
                $invertScaleQuality = 9 - round((80/100) * 9);
                imagepng($thumb_create, $thumbnail, $invertScaleQuality);
            break;

            case 'gif':
                imagegif($thumb_create, $thumbnail, 80);
            break;

            default:
                imagejpeg($thumb_create, $thumbnail, 80);
        }

        imagedestroy($source);
        imagedestroy($thumb_create);
    }

?>