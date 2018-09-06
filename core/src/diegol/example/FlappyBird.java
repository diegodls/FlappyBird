package diegol.example;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

import sun.rmi.runtime.Log;

public class FlappyBird extends ApplicationAdapter {


    //SpriteBatch batch;
    //Texture img;
    private SpriteBatch batch;
    private Texture[] passaro;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoCima;
    private Texture gameOver;
    private BitmapFont fonte;
    private BitmapFont mensagemGameOver;
    private Circle passaroCirculo;
    private Rectangle canoCimaR;
    private Rectangle canoBaixoR;
    private ShapeRenderer shape;

    //Atributos da configuração
    private int movimento = 0;
    private float larguraDispositivo;
    private float alturaDispositivo;
    private int estadoJogo = 0; // 0 = parado, 1 = iniciado, 2 = game over
    private int pontuacao = 0;


    private float velocidadeQuedaPassaro = 0;
    private float alturaInicial;
    private float posicaoMovimentoCanoHorizontal;
    private float espacoEntreCanos;
    private float deltaTime;
    private Random numRandom;
    private float alturaEnteCanosRandom;
    private boolean pontuou;

    private float variacao_img = 0;

    //camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGHT = 1024;

    @Override
    public void create() {
        batch = new SpriteBatch();
        passaro = new Texture[3];
        passaro[0] = new Texture("passaro1.png");
        passaro[1] = new Texture("passaro2.png");
        passaro[2] = new Texture("passaro3.png");
        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo_maior.png");
        canoCima = new Texture("cano_topo_maior.png");
        gameOver = new Texture("game_over.png");
        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);
        passaroCirculo = new Circle();
        mensagemGameOver = new BitmapFont();
        mensagemGameOver.setColor(Color.WHITE);
        mensagemGameOver.getData().setScale(3);
        //canoBaixoR = new Rectangle();
        //canoCimaR = new Rectangle();
        //shape = new ShapeRenderer();

        numRandom = new Random();

        camera = new OrthographicCamera();
        //regulagem da posição da camera caso fique bugado
        camera.position.set(VIRTUAL_WIDTH/2,VIRTUAL_HEIGHT/2,0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

        alturaDispositivo = VIRTUAL_HEIGHT;
        larguraDispositivo = VIRTUAL_WIDTH;

        alturaInicial = alturaDispositivo / 2;
        posicaoMovimentoCanoHorizontal = larguraDispositivo;
        espacoEntreCanos = 300;



    }

    @Override
    public void render() {

        camera.update();
        // limpar frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (variacao_img > 2) {
            variacao_img = 0;
        }
        deltaTime = Gdx.graphics.getDeltaTime();

        if (estadoJogo == 0) {//não iniciado
            if (Gdx.input.justTouched()) {
                estadoJogo = 1;
            }
        } else {//iniciado

            velocidadeQuedaPassaro++;
            if (alturaInicial > 0 || velocidadeQuedaPassaro < 0) {
                alturaInicial = alturaInicial - velocidadeQuedaPassaro;
            }

            if (estadoJogo == 1) {

                posicaoMovimentoCanoHorizontal -= deltaTime * 200;
                if (Gdx.input.justTouched()) {

                    velocidadeQuedaPassaro = -15;
                }

                //verifica se o cano sai do cenário
                if (posicaoMovimentoCanoHorizontal < -canoBaixo.getWidth()) {
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEnteCanosRandom = numRandom.nextInt(1000) - 500;
                    pontuou = false;
                }

                //verifica se o cano passou do passaro
                if (posicaoMovimentoCanoHorizontal < 120) {
                    if (!pontuou) {

                        pontuacao++;
                        pontuou = true;
                    }

                }
            }else{//game over


                if(Gdx.input.justTouched()){
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQuedaPassaro = 0;
                    alturaInicial = alturaDispositivo / 2;
                    posicaoMovimentoCanoHorizontal = larguraDispositivo;
                    alturaEnteCanosRandom = numRandom.nextInt(1000) - 500;
                }
            }
        }

        //configurar camera
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);

        //batch.draw(canoCima, posicaoMovimentoCanoHorizontal, alturaDispositivo - canoCima.getHeight());
        batch.draw(canoCima, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEnteCanosRandom);
        batch.draw(canoBaixo, posicaoMovimentoCanoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEnteCanosRandom);
        batch.draw(passaro[(int) variacao_img], 120, alturaInicial);

        if (estadoJogo == 2){
            batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth() / 2, alturaDispositivo / 2);
            mensagemGameOver.draw(batch, "Toque para reiniciar", larguraDispositivo / 2 - 200, alturaDispositivo / 2 - gameOver.getHeight() / 2);
        }

        if (movimento == larguraDispositivo) {
            movimento = -5;
        }


        variacao_img += deltaTime * 10;



        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50);
        batch.end();

        passaroCirculo.set(120 + passaro[0].getWidth() / 2, alturaInicial + passaro[0].getHeight() / 2, passaro[0].getWidth() / 2);
        canoBaixoR = new Rectangle(
                posicaoMovimentoCanoHorizontal,
                alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCanos / 2 + alturaEnteCanosRandom,
                canoBaixo.getWidth(),
                canoBaixo.getHeight());

        canoCimaR = new Rectangle(
                posicaoMovimentoCanoHorizontal,
                alturaDispositivo / 2 + espacoEntreCanos / 2 + alturaEnteCanosRandom,
                canoCima.getWidth(),
                canoCima.getHeight());

        //Desenhar formas
        /*shape.begin(ShapeRenderer.ShapeType.Filled);
        shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
        shape.rect(canoBaixoR.x,canoBaixoR.y,canoBaixoR.width,canoBaixoR.height);
        shape.rect(canoCimaR.x,canoCimaR.y,canoCimaR.width,canoCimaR.height);
        shape.setColor(Color.RED);

        shape.end();*/

        //Teste de colisão
        if (Intersector.overlaps(passaroCirculo, canoBaixoR)
                || Intersector.overlaps(passaroCirculo, canoCimaR)
                || alturaInicial <= 0
                || alturaInicial >= alturaDispositivo) {

            estadoJogo = 2;

        }


    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }

    @Override
    public void dispose() {

    }


}
