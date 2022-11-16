package scc.srv;

import scc.utils.Hash;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;


/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource {

	///String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=scctp1storage;AccountKey=QqhHmfz4oH+OgB7vZ26+2VvzoqlqoV7zJt1ioGy36D5zyKJMf5iyjKp/nBx1+iQJX9zZ/378/SJA+AStSmShug==;EndpointSuffix=core.windows.net";
	String storageConnectionString = System.getenv("BLOB_STORE_CONNECTION");
	BlobContainerClient containerClient = new BlobContainerClientBuilder().connectionString(storageConnectionString).containerName("images").buildClient();


	/**
	 * Post a new image.The id of the image is its hash.
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public String upload(byte[] contents) {
		String key = Hash.of(contents);
		try {
			BinaryData data = BinaryData.fromBytes(contents);
			BlobClient blob = containerClient.getBlobClient(key+".jpeg");
			blob.upload(data);
			System.out.println("File updloaded : file" + key);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return key;

	}

	/**
	 * Return the contents of an image. Throw an appropriate error message if id
	 * does not exist.
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] download(@PathParam("id") String id) {
		BinaryData data = null;
		try {
			BlobClient blob = containerClient.getBlobClient(id);
			data = blob.downloadContent();
		} //catch (NotFoundException e) {
		//	System.out.println("Not found");
		//}
		 catch(Exception e){
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return data.toBytes();
	}

	/**
	 * Lists the ids of images stored.
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> list() {
		List<String> aux = new ArrayList<String>();
		for (BlobItem blobItem : containerClient.listBlobs()) {
			System.out.println("\t" + blobItem.getName());
			aux.add(blobItem.getName());
		}
		return aux;
	}

}
