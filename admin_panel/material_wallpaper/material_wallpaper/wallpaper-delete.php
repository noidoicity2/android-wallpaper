<?php ob_start(); ?>
<?php include_once('includes/header.php'); ?>

<?php
	
	if (isset($_GET['id'])) {
		$ID = clean($_GET['id']);
	} else {
		$ID = clean("");
	}

	// get image file from table
	$sql = "SELECT type, image FROM tbl_gallery WHERE id = '$ID'";
	$result = $connect->query($sql);
	$row = $result->fetch_assoc();

	$type = $row['type'];
	$image = $row['image'];

	// delete data from menu table
	$sql_delete = "DELETE FROM tbl_gallery WHERE id = '$ID'";
	$delete = $connect->query($sql_delete);

	// if delete data success
	if ($delete) {

		if ($type == 'upload') {
			$delete_image = unlink('upload/'.$image);
			$delete_image = unlink('upload/thumbs/'.$image);
		}

		$_SESSION['msg'] = "Wallpaper deleted successfully...";
	    header( "Location: wallpaper.php");
	     exit;
	}

?>

<?php include_once('includes/footer.php'); ?>