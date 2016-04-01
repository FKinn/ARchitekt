package eu.kudan.kudansamples;

import eu.kudan.kudan.ARLightMaterial;
import eu.kudan.kudan.ARMeshNode;
import eu.kudan.kudan.ARModelImporter;
import eu.kudan.kudan.ARModelNode;
import eu.kudan.kudan.ARTexture2D;
import eu.kudan.kudan.ARTextureMaterial;

/**
 * Created by Florian Kinn on 28.03.2016.
 *
 * model initialization happens here
 * */
public class Models {

    static ARModelNode modelNode;

    public static ARModelNode setupModel(String model){

        switch (model){
            case "Neugereut":
                setupNeugereut();
            case "Bloodhound":
                setupBloodhound();
            case "Wall":
                setupWall();
            case "Fence":
                setupFence();
            default:
                break;

        }
        return modelNode;
    }

    private static void setupBloodhound() {
        ARModelImporter importer = new ARModelImporter();
        importer.loadFromAsset("bloodhound.jet");
        modelNode = (ARModelNode) importer.getNode();
        ARTexture2D texture2D = new ARTexture2D();
        texture2D.loadFromAsset("bloodhound.png");
        ARLightMaterial material = new ARLightMaterial();
        material.setTexture(texture2D);

        material.setDiffuse(0.2f, 0.2f, 0.2f);
        material.setAmbient(0.8f, 0.8f, 0.8f);
        material.setSpecular(0.3f, 0.3f, 0.3f);
        material.setShininess(20.0f);
        material.setReflectivity(0.15f);

        //Vector3f lightDirection = new Vector3f(0.0f, -1.0f, 0.0f);
        for (ARMeshNode meshNode : importer.getMeshNodes()) {
            meshNode.setMaterial(material);
            //meshNode.setLightDirection(lightDirection);
        }

        modelNode.scaleByUniform(6.0f);
        modelNode.setVisible(true);
    }


    private static void setupWall(){


        ARModelImporter importer = new ARModelImporter();
        importer.loadFromAsset("wall.jet");
        modelNode = (ARModelNode) importer.getNode();
        ARTexture2D texture2D = new ARTexture2D();
        texture2D.loadFromAsset("wall.png");
        ARTextureMaterial material = new ARTextureMaterial();
        material.setTexture(texture2D);
        for (ARMeshNode meshNode : importer.getMeshNodes()) {
            meshNode.setMaterial(material);
        }

		/* alternative to give every meshNode a different texture
		for (int i = 0; i < importer.getMeshNodes().size(); i++){
			ARTexture2D texture2D = new ARTexture2D();
			texture2D.loadFromAsset("army"+i+".png");
			ARTextureMaterial material = new ARTextureMaterial();
			material.setTexture(texture2D);
			importer.getMeshNodes().get(i).setMaterial(material);
		}
		*/
        modelNode.scaleByUniform(6.0f);
        modelNode.setVisible(true);

    }


    private static void setupNeugereut() {
        ARModelImporter importer = new ARModelImporter();
        importer.loadFromAsset("neugereut.jet");
        modelNode = (ARModelNode) importer.getNode();

        /* alternative to give every meshNode a different texture
        *
        * u = workaround to get different textures on different nodes
        * */
        int u = 0;
        for (int i = 0; i < importer.getMeshNodes().size(); i++){
            if(u > 3 ){
                u = 0;
            }
            ARTexture2D texture2D = new ARTexture2D();
            texture2D.loadFromAsset("beton"+u+".png");
            ARTextureMaterial material = new ARTextureMaterial();
            material.setTexture(texture2D);
            importer.getMeshNodes().get(i).setMaterial(material);
            u++;
        }


        modelNode.rotateByDegrees(-90, 1.0f, 0.0f, 0.0f);
        modelNode.scaleByUniform(3.0f);
        modelNode.setVisible(true);
    }

    private static void setupFence(){
        ARModelImporter importer = new ARModelImporter();
        importer.loadFromAsset("fence.jet");
        modelNode = (ARModelNode) importer.getNode();

        for (int i = 0; i < importer.getMeshNodes().size(); i++){
            ARTexture2D texture2D = new ARTexture2D();
            texture2D.loadFromAsset("fence" + i + ".png");
            ARTextureMaterial material = new ARTextureMaterial();
            material.setTexture(texture2D);
            material.setTransparent(true);
            importer.getMeshNodes().get(i).setMaterial(material);
        }
        modelNode.scaleByUniform(6.0f);
        modelNode.setVisible(true);
    }
}
