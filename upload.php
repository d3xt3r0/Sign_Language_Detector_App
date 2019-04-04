<?php
    $target_dir = "files/"; //folder name where your files will be stored. create this folder inside "file_upload_api" folder
    $target_file = $target_dir . basename($_FILES["file"]["name"]);
    $response = array('success' => false, 'message' => 'Sorry, there was an error uploading your file.');
    $data = $_POST['sender_information'];
    $json_data = json_decode($data , true);
    $sender_name = $json_data['sender_name'];
    //$sender_age = $json_data['sender_age'];
	
	
	if (move_uploaded_file($_FILES["file"]["tmp_name"], $target_file)) {
		chdir(".\\Sign-Language-Recognition-master\\Sign-Language-Recognition\\code\\");
		$pyscript = "predict_from_file_modified.py";
		//$python = "C:\\Python27\\python.exe";
		$python = "python";
		$svm = "svm";
		$cod = "$python $pyscript $svm";
		$output = shell_exec("python predict_from_file_modified.py svm 2>&1 ");
		
		preg_match_all('#labelx=([^\s]+)#', $output, $matches);
		$result = implode(' ', $matches[1]);
        $response = array('success' => true, 'message' => "$result");
	}
    echo json_encode($response);
    
	
	
	
	
	
	//echo json_encode("$result_mod");
	//echo json_encode($result);
	//echo "$output hello";
	//echo "$output";
	//$python $pyscript $svm
	
?>