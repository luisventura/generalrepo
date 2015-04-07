package korinver.com.ecomanda;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class adminComanda extends ActionBarActivity implements communicator{
    //declaraciones de cadenas constantes para parsing de comanda recibida
    //tags de apertura
    private static final String TAG_COMANDA     = "comanda";
    private static final String TAG_PERSONAS    = "personas";
    private static final String TAG_DETALLE     = "detalle";

    //tags array encabezado
    private static final String TAG_IDSUCURSAL  = "idsucursal";
    private static final String TAG_NMESA       = "nmesa";
    private static final String TAG_RECNOPISO   = "recno_piso";
    private static final String TAG_IDEMPLEADO  = "id_empleado";
    private static final String TAG_NPERSONAS   = "npersonas";
    private static final String TAG_RECNOENC    = "recno";
    private static final String TAG_ESTADO      = "estado";

    //tags array detalle
    private static final String TAG_NPERSONA    = "npersona";
    private static final String TAG_IDPLATO     = "id_plato";
    private static final String TAG_RECNODET    = "recno";
    private static final String TAG_GENERO      = "sexo";
    private static final String TAG_EDAD        = "rango_edad";
    private static final String TAG_PRECIO      = "precio";
    private static final String TAG_ENTREGADO   = "entregado";

    //variables auxiliares
    boolean comandaClickFlag=true;

    //declaraciones de comanda

    private ArrayList<persona> clienteEnviado       = new ArrayList<persona>();         //estructura que almacena los datos de cada cliente en la comanda que se ha recibido del servidor
    private ArrayList<persona> clienteCreacion      = new ArrayList<persona>();        //estructura que almacena los datos de cada cliente en la comanda que se estacreando
    private ArrayList<personaUI> clienteEnviadoUI   = new ArrayList<personaUI>();   //estructura que almacena los elementos de la UI de cada cliente en la comanda que se ha recibido del servidor
    private ArrayList<personaUI> clienteCreacionUI  = new ArrayList<personaUI>(); //estructura que almacena los elementos de la UI de cada cliente en la comanda que se esta creando

    //variables de menu del restaurante
    private ArrayList<String> listaCategorias;          //almacena las strings de las categorias que se muestran en el listview de categorias
    private ArrayList<Integer> arregloCategorias;       //almacena el recno de las categorías del menu en un array secuencial. NOTA: Posición 1 corresponderá siempre a las promociones.
    private HashMap<Integer, categoria> mapaCategorias; //almacena los datos de las categorias (nombre y recno) utilizando su recno como key (redundancia por cuestiones de facilidad de busqueda)
    private HashMap<Integer, plato> mapaPlatos;         //almacena los datos de los platillos (nombre, recno y recno de su categoria) utilizando su recno como key (redundancia por cuestiones de facilidad de busqueda)

    //datos de manejo de mesas
    private String idSucursal           = "";   //almacena el string de la sucursal a la que pertenece la tablet, consiste de 3 numeros.
    private String numeroMesa           = "";   //almacena el string del numero de mesa que se esta administrando
    private String idCadena             = "";   //almacena el string de conexion encriptado de la cadena a la que pertenece, para autenticación
    private String urlservidor          = "";   //almacena el string de la url base del servidor que provee la información a la tablet
    private String idMesero             = "";   //almacena el id del mesero que esta logeado actualmente y crea una comanda, para auditoria e inteligencia,
    private String nombreMesero         = "";    //almacena el string del nombre del mesero actual
    private int estadoMesa              = 0;    //almacena el estado de la mesa
    private int numeroPersonas          = 0;    //numero de personas actualmente en la mesa, inicia de 0.
    private int personaSeleccionada     = 0; //numero de la persona seleccionada para insertarle platos
    private int categoriaSeleccionada   = 0; //categoria seleccionada para validar despliegue de sus platillos
    private int pestanaAbierta          = 0;          //bandera de validacion para apertura/cierre/cambio de categoria
    private int pestanaPlatoAbierta     = 0;
    private int puntosSuspensivos=0;         //sirve para manejar los puntos suspensivos que se animan en el titulo de la actividad

    //lista de categorias
    private ListView listaCat;
    private ArrayAdapter<String> listAdapter;


    //elementos visuales
    private LinearLayout ll;            //linear layout que contiene la comanda en creacion
    private LinearLayout ll2;           //linear layout que contiene la comanda recibida del servidor
    private RelativeLayout rlenv;       //relative layout que contiene la comanda recibida del servidor
    private RelativeLayout rlcrea;      //relative layout que contiene la comanda en creacion

    //lista de personas

    private fragmentComandaPlatillos frag = new fragmentComandaPlatillos(); //fragmento que carga las categorias de los platillos
    private fragmentComandaOpciones frag2 = new fragmentComandaOpciones();
    private FragmentManager manager = getFragmentManager();

    Button facturacion;
    TextView textoCargando;
    TextView textoMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        //recibimos datos de actividad anterior: numero de mesa
        Intent intent = getIntent();
        numeroMesa    = intent.getStringExtra("Id_mesa");
        nombreMesero  = intent.getStringExtra("nombre_mesero");
        //=Integer.parseInt(intent.getStringExtra("status"));
        //=Integer.parseInt(intent.getStringExtra("numeroPersonasOAsientos"));

        //final String titulo = nombreMesero + " " + "atendiendo en mesa N°"+ " " + numeroMesa;
        final String titulo = nombreMesero + " " + getString(R.string.atendiendo_en_mesa) + " " + numeroMesa;
        this.setTitle(titulo);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTitle(getTitle() + ".");
                        setPuntos(getPuntos()+1);
                        if(getPuntos()==4) {
                            setPuntos(0);
                            setTitle(titulo);
                        }
                    }
                });
            }
        };

        timer.schedule(timerTask, 1000, 1000);

        //----------------------------------------------------

        setContentView(R.layout.activity_admin_comanda);


        //declaramos elementos visuales
        //nMesero= (TextView) findViewById(R.id.imageView);
        FrameLayout frameComandaEnvTitle = (FrameLayout) findViewById(R.id.comandaenvtitle);
        FrameLayout frameComandaActTitle = (FrameLayout) findViewById(R.id.comandaacttitle);
        ll  = (LinearLayout) findViewById(R.id.comandaAct);
        ll2 = (LinearLayout) findViewById(R.id.comandaEnv);
        rlcrea = (RelativeLayout) findViewById(R.id.relativeCreacion);
        rlenv = (RelativeLayout) findViewById(R.id.relativeEnviado);
        Button enviarComanda = (Button) findViewById(R.id.buttonEnviar);
        final Button anadirPersona = (Button) findViewById(R.id.button2);
        listaCat = (ListView) findViewById(R.id.listView_categorias);
        facturacion = (Button) findViewById(R.id.button4);
        textoMensaje = (TextView) findViewById(R.id.textViewMENSAJE);
        textoCargando = (TextView) findViewById(R.id.textViewCARGANDO);

        //instanciamos estructuras
        listaCategorias = new ArrayList<String>();
        arregloCategorias = new ArrayList<Integer>();
        mapaCategorias = new HashMap<Integer, categoria>();
        mapaPlatos = new HashMap<Integer, plato>();



//        nMesero.setText("Nombre: " + nombreMesero);
//        tvmesa.setText("Mesa #" + numeroMesa);


        //consultamos menu a la base de datos

        consultasadb();

        listAdapter = new ArrayAdapter<String>(this, R.layout.fragmentplatillo_rowplatillo, listaCategorias);
        listaCat.setAdapter(listAdapter);
//        listaCat.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_listviews_comanda));

        //consulta de comanda actual de mesa

        new cargarComanda().execute();

        //----------------

        frameComandaActTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCreacion();
            }
        });
        frameComandaEnvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarEnviado();
                while(clienteEnviado.size()!=0){
                    eliminarPersonaComanda(ll2, clienteEnviadoUI,clienteEnviado,clienteEnviado.size()-1);
                    System.out.println("tamaño de enviado  = " + clienteEnviado.size());
                }
                new cargarComanda().execute();
            }
        });

        listaCat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("indexCategoria", position);
                ArrayList<Integer> listaRecnosPlat = new ArrayList<Integer>();
                ArrayList<String> listaStringsPlat = new ArrayList<String>();


                for (int a = 0; a < mapaCategorias.get(arregloCategorias.get(position)).getPlatosEnEstaCategoria().size(); a++) {
                    listaRecnosPlat.add(mapaCategorias.get(arregloCategorias.get(position)).getPlatosEnEstaCategoria().get(a).getRecno());
                    listaStringsPlat.add(mapaCategorias.get(arregloCategorias.get(position)).getPlatosEnEstaCategoria().get(a).getNombre_plato());
                }
                bundle.putStringArrayList("listaStringsPlat", listaStringsPlat);
                bundle.putIntegerArrayList("listaRecnosPlat", listaRecnosPlat);
                bundle.putString("categoria", listaCategorias.get(position));
                if (pestanaAbierta == 0) {
                    FragmentTransaction transaction = manager.beginTransaction();
                    frag.setArguments(bundle);
//                    transaction.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
                    transaction.add(R.id.relativefragment, frag, "fragplatillos");
                    //transaction.add(R.id.baseElementComanda, frag, "fragplatillos");
                    //transaction.addToBackStack(null);
                    if(pestanaPlatoAbierta==1){
                        transaction.remove(frag2);
                        pestanaPlatoAbierta=0;
                    }
                    transaction.commit();
                    frag.setCategoria(categoriaSeleccionada);
                    parent.getChildAt(position).setBackgroundColor(0xFFFFCC77);
                    pestanaAbierta = 1;
                    mostrarCreacion();

                } else {
                    if (categoriaSeleccionada == position) {
                        FragmentTransaction transaction = manager.beginTransaction();
                        frag = (fragmentComandaPlatillos) manager.findFragmentByTag("fragplatillos");
                        transaction.remove(frag);
                        transaction.commit();
                        pestanaAbierta = 0;
                        parent.getChildAt(position).setBackgroundColor(0x00009999);
                    } else {
                        frag = (fragmentComandaPlatillos) manager.findFragmentByTag("fragplatillos");
                        frag.cargarPlatosCategoria(listaStringsPlat, listaRecnosPlat, listaCategorias.get(position));
                        frag.setCategoria(categoriaSeleccionada);
                        parent.getChildAt(categoriaSeleccionada).setBackgroundColor(0x00009999);
                        parent.getChildAt(position).setBackgroundColor(0xFFFFCC77);
                    }
                }
                categoriaSeleccionada = position;
            }
        });

        enviarComanda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new enviarComanda().execute();
            }
        });
        anadirPersona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anadirPersonaComanda(ll, clienteCreacionUI,clienteCreacion, true, 3, "M");
            }
        });
        facturacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DecimalFormat df;
                DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
                simbolos.setDecimalSeparator('.');
                df = new DecimalFormat("0.00",simbolos);
                aplicacion.nmesa = numeroMesa;
                aplicacion.mtofactura = new ArrayList<MtoFactura>();
                aplicacion.mtofactura_indi = new ArrayList<MtoFactura>();
                if (clienteEnviado.size() == 0) {
                    Toast.makeText(getApplicationContext(), "No se puede facturar si no hay platillos enviados", Toast.LENGTH_SHORT).show();
                } else {
                    if(revisarplatosentregados()){
                        for (int i = 0; i < clienteEnviado.size(); i++) {
                            for (int b = 0; b < clienteEnviado.get(i).getListaPlatillos().size(); b++) {
                                aplicacion.rellenarArrayList(getApplicationContext(), String.valueOf(clienteEnviado.get(i).getNumeroPersona() + 1), String.valueOf(clienteEnviado.get(i).getListaPlatillos().get(b).getRecno()), String.valueOf(df.format(clienteEnviado.get(i).getListaPlatillos().get(b).getPrecioAcumulado())));
                                aplicacion.rellenarArrayList_basico(getApplicationContext(), String.valueOf(clienteEnviado.get(i).getNumeroPersona() + 1), String.valueOf(clienteEnviado.get(i).getListaPlatillos().get(b).getRecno()), String.valueOf(df.format(clienteEnviado.get(i).getListaPlatillos().get(b).getPrecioAcumulado())));
                            }
                        }

                        Intent i = new Intent(adminComanda.this, Facturacion.class);
                        i.putExtra("Id_mesa", numeroMesa);
                        i.putExtra("nombre_mesero", nombreMesero);
                        startActivity(i);
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Aun hay platos sin entregar, imposible facturar.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_admin_comanda, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Toast.makeText(getApplicationContext(),""+item.getItemId(),Toast.LENGTH_SHORT).show();
        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return true;
    }

    public String comandaaxml() throws IllegalArgumentException, IllegalStateException, IOException {
        String comandaenxml = new String();
        StringWriter writer = new StringWriter();
        XmlSerializer serializer = Xml.newSerializer();
        serializer.setOutput(writer);

        serializer.startDocument("UTF8", true);
        serializer.startTag("", "comanda");

        serializer.startTag("", "idsucursal");
        serializer.text("" + idSucursal);
        serializer.endTag("", "idsucursal");

        serializer.startTag("", "nmesa");
        serializer.text("" + numeroMesa);
        serializer.endTag("", "nmesa");

        serializer.startTag("", "idempleado");
        serializer.text("" + idMesero);
        serializer.endTag("", "idempleado");

        serializer.startTag("", "npersonas");
        serializer.text("" + clienteCreacion.size());
        //serializer.text("" + 6);
        serializer.endTag("", "npersonas");

        serializer.startTag("", "tipo_comanda");
        serializer.text("" + 3);
        serializer.endTag("", "tipo_comanda");

        for (int a = 0; a < clienteCreacion.size(); a++) {
            serializer.startTag("", "persona");

            serializer.startTag("", "npersona");
            serializer.text("" + (a + 1));
            serializer.endTag("", "npersona");

            serializer.startTag("", "genero");
            serializer.text("" + clienteCreacion.get(a).getGenero());
            serializer.endTag("", "genero");

            serializer.startTag("", "rango_edad");
            serializer.text("" + clienteCreacion.get(a).getRangoEdad());
            serializer.endTag("", "rango_edad");

            for(int b = 0; b < clienteCreacion.get(a).getListaPlatillos().size(); b++) {

                serializer.startTag("", "plato");

                serializer.startTag("", "idplato");
                serializer.text("" + clienteCreacion.get(a).getListaPlatillos().get(b).getRecno());
                serializer.endTag("", "idplato");

                for (int c = 0; c < clienteCreacion.get(a).getListaPlatillos().get(b).getOpcionesPlato().size(); c++) {
                    serializer.startTag("", "opcion");
                    serializer.text("" + clienteCreacion.get(a).getListaPlatillos().get(b).getOpcionesPlato().get(c));
                    serializer.endTag("", "opcion");
                }

                if(!clienteCreacion.get(a).getListaPlatillos().get(b).getComentario().equals("")){

                    serializer.startTag("", "opcionm");

                    serializer.startTag("", "idopcion");
                    serializer.text("0");
                    serializer.endTag("", "idopcion");

                    serializer.startTag("", "seleccion");
                    Toast.makeText(getApplicationContext(),clienteCreacion.get(a).getListaPlatillos().get(b).getComentario(),Toast.LENGTH_LONG).show();
                    serializer.text("" + clienteCreacion.get(a).getListaPlatillos().get(b).getComentario());
                    serializer.endTag("", "seleccion");

                    serializer.endTag("", "opcionm");

                }
                serializer.endTag("", "plato");
            }

            serializer.endTag("", "persona");
        }

        serializer.endTag("", "comanda");
        serializer.endDocument();
        comandaenxml = writer.toString();

        System.out.println(comandaenxml);
        return comandaenxml;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, AdminMesas.class);
        i.putExtra("nombre_empleado", nombreMesero);
        startActivity(i);
        finish();
    }

    public void anadirPersonaComanda(LinearLayout ll, final ArrayList<personaUI> listaPersonasUI, ArrayList<persona> listaPersonas, boolean seleccionable,int rangoEdad, String genero){
        //parte datos
        listaPersonas.add(new persona(listaPersonas.size(), rangoEdad, genero));

        //parte grafica y seleccion de cliente
        listaPersonasUI.add(new personaUI(ll, getApplicationContext(), listaPersonasUI.size(),listaPersonas.get(listaPersonas.size() - 1)));
        final int a = listaPersonasUI.get(listaPersonasUI.size()-1).getNumeroCliente();
        if(seleccionable) {
            listaPersonasUI.get(listaPersonasUI.size() == 0 ? 0 : listaPersonasUI.size() - 1).getFl().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listaPersonasUI.get(personaSeleccionada).deseleccionarPersonaUI();
                    listaPersonasUI.get(a).seleccionarPersonaUI();
                    personaSeleccionada = a;
                }
            });
        }else{
            listaPersonasUI.get(a).getTxtswtedad().setOnClickListener(null);
            listaPersonasUI.get(a).getTxtswtgenero().setOnClickListener(null);
        }
    }

    public void eliminarPersonaComanda(LinearLayout ll, final ArrayList<personaUI> listaPersonasUI, ArrayList<persona> listaPersonas, int personaABorrar){
        ll.removeView(listaPersonasUI.get(personaABorrar).getFl());
        listaPersonasUI.remove(personaABorrar);
        listaPersonas.remove(personaABorrar);
    }

    public void anadirPlatoComanda(final ArrayList<personaUI> listaPersonasUI, final ArrayList<persona> listaPersonas, Context context, int recno, final int recnoDet,int entregado, final int personaAAniadir, final double precio, String comentario, ArrayList<Integer> opciones){
        //datos
        listaPersonas.get(personaAAniadir).anadirPlatillo(new platillo(recno, entregado, recnoDet, precio, comentario, opciones));

        //parte grafica
        listaPersonasUI.get(personaAAniadir).anadirPlatilloUI(mapaPlatos.get(recno).getRecnoCat(), mapaCategorias.get(mapaPlatos.get(recno).getRecnoCat()).getNomcategoria(), mapaPlatos.get(recno).getNombre_plato(), context, personaAAniadir);

        switch(entregado){
            case -1:    listaPersonasUI.get(personaAAniadir).getRowsPlatillos().get(listaPersonasUI.get(personaAAniadir).getRowsPlatillos().size()-1).getCb().setVisibility(View.GONE);
                         break;
            case 0:     listaPersonasUI.get(personaAAniadir).getRowsPlatillos().get(listaPersonasUI.get(personaAAniadir).getRowsPlatillos().size()-1).getButton().setVisibility(View.GONE);
                        listaPersonasUI.get(personaAAniadir).getRowsPlatillos().get(listaPersonasUI.get(personaAAniadir).getRowsPlatillos().size()-1).getCb().setChecked(false);
                         break;
            case 1:     listaPersonasUI.get(personaAAniadir).getRowsPlatillos().get(listaPersonasUI.get(personaAAniadir).getRowsPlatillos().size()-1).getCb().setChecked(true);
                        listaPersonasUI.get(personaAAniadir).getRowsPlatillos().get(listaPersonasUI.get(personaAAniadir).getRowsPlatillos().size()-1).getButton().setVisibility(View.GONE);
                         break;
        }
        final int detalleComanda = listaPersonas.get(personaAAniadir).getListaPlatillos().size()-1;
        //final int estadoEntrega  = listaPersonas.get(personaAAniadir).getListaPlatillos().get(detalleComanda).getEntregado();

        listaPersonasUI.get(personaAAniadir).getRowsPlatillos().get(listaPersonasUI.get(personaAAniadir).getRowsPlatillos().size()-1).getCb().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpHandler llamadaEntregado = new httpHandler();
                llamadaEntregado.params.add(new BasicNameValuePair("p0", idCadena));
                llamadaEntregado.params.add(new BasicNameValuePair("p1", "" + recnoDet));
                if(listaPersonas.get(personaAAniadir).getListaPlatillos().get(detalleComanda).getEntregado()==0){
                    String respuesta = llamadaEntregado.post(urlservidor + "entregar_plato.php");
                    listaPersonas.get(personaAAniadir).getListaPlatillos().get(detalleComanda).setEntregado(1);
                    Toast.makeText(getApplicationContext(), "Entrega Exitosa " + respuesta, Toast.LENGTH_SHORT).show();
                }else{
                    if(listaPersonas.get(personaAAniadir).getListaPlatillos().get(detalleComanda).getEntregado()==1){
                        String respuesta = llamadaEntregado.post(urlservidor + "devolver_plato.php");
                        listaPersonas.get(personaAAniadir).getListaPlatillos().get(detalleComanda).setEntregado(0);
                        Toast.makeText(getApplicationContext(), "Devolución Exitosa " + respuesta, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        /*listaPersonasUI.get(personaAAniadir).getRowsPlatillos().get(listaPersonasUI.get(personaAAniadir).getRowsPlatillos().size()-1).getCb().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new reportePlato().execute(recnoDet, estadoEntrega, personaAAniadir, detalleComanda);
            }
        });*/
        setClickListeners(personaAAniadir, listaPersonasUI);
    }

    public void eliminarPlatoComanda(ArrayList<personaUI> listaPersonasUI, ArrayList<persona> listaPersonas, int recno, int dueno){
        //parte grafica
        listaPersonasUI.get(dueno).removerPlatilloUI(recno);
        //parte datos
        listaPersonas.get(dueno).eliminarPlatillo(recno);
        setClickListeners(dueno, listaPersonasUI);
    }

    public void setClickListeners(final int dueno,final ArrayList<personaUI> listaPersonasUI){
        for(int a=0; a<listaPersonasUI.get(dueno).getRowsPlatillos().size();a++){
            final int b=listaPersonasUI.get(dueno).getRowsPlatillos().get(a).posicion;
            listaPersonasUI.get(dueno).getRowsPlatillos().get(a).getButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminarPlatoComanda(listaPersonasUI, clienteCreacion, b,dueno);
                }
            });
        }
    }

    public void obtenerComandayparse(String idCadena, String idSucursal, String numeroMesa) throws JSONException{
        httpHandler llamadaAComanda = new httpHandler();
        llamadaAComanda.params.add(new BasicNameValuePair("p0", idCadena));
        llamadaAComanda.params.add(new BasicNameValuePair("p1", idSucursal));
        llamadaAComanda.params.add(new BasicNameValuePair("p2", numeroMesa));
        String comandaRecibida = llamadaAComanda.post(urlservidor + "consultar_comanda.php");
        System.out.println("resultado comanda recibida " + comandaRecibida);
        if(!comandaRecibida.equals("-1")){ //aqui hay excepciones en mala conectividad
            try {
                JSONObject comandaJson = null;
                comandaJson = new JSONObject(comandaRecibida);
                llenarComandaRecibida(comandaJson);
            }
            catch(Exception e) {
                System.out.println(e);
            }
        }else{
            Toast.makeText(getApplicationContext(),"MESA LIBRE SIN COMANDA", Toast.LENGTH_SHORT).show();
        }
    }

    public void llenarComandaRecibida(JSONObject comandaJson) throws JSONException{
        int numero=0;
        JSONObject enc = comandaJson.getJSONObject(TAG_COMANDA);
        numero = enc.getInt(TAG_NPERSONAS);
        JSONArray per = comandaJson.getJSONArray(TAG_PERSONAS);
        JSONArray det = comandaJson.getJSONArray(TAG_DETALLE);
        numeroPersonas = numero;
        for (int a=0; a<numero; a++){
            JSONObject pertemp = per.getJSONObject(a);
            String genero = pertemp.getString(TAG_GENERO);
            int rango = pertemp.getInt(TAG_EDAD);
            anadirPersonaComanda(ll2,clienteEnviadoUI, clienteEnviado, false, rango, genero );
            if(clienteCreacion.size()<(numero)){
                anadirPersonaComanda(ll,clienteCreacionUI, clienteCreacion, true, rango , genero);
            }
        }

        for(int a=0; a<det.length();a++){
            JSONObject personaTemp = det.getJSONObject(a);
            int numPersonaTemp = personaTemp.getInt(TAG_NPERSONA);
            int recno =personaTemp.getInt(TAG_IDPLATO);
            int recnoDet =personaTemp.getInt(TAG_RECNODET);
            int entregado = personaTemp.getInt(TAG_ENTREGADO);
            double precio = personaTemp.getDouble(TAG_PRECIO);
            //aqui se introduce el plato parseado con metodo anadirplatocomanda
            anadirPlatoComanda(clienteEnviadoUI,clienteEnviado,getApplicationContext(),recno, recnoDet, entregado,numPersonaTemp-1,precio, "", null);
        }
    }

    //hace los queries a base de datos y carga tod o en las estructuras destinadas para ello.
    public void consultasadb(){
        AdminSQLiteOpenHelper admin = null;
        SQLiteDatabase bd = null;

        try {
            admin = new AdminSQLiteOpenHelper(getApplicationContext(), "ecomanda", null, 1);
            bd = admin.getWritableDatabase();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error abriendo base de datos",Toast.LENGTH_SHORT).show();
            System.out.println(e);
        }

        try {
            Cursor cadena_p0 = bd.rawQuery("select nombrevari, valorvari from config", null);
            cadena_p0.moveToFirst();

            for(int a=0; a<cadena_p0.getCount();a++){
                if(cadena_p0.getString(0).equals("id_cadena"))   idCadena = cadena_p0.getString(1);
                if(cadena_p0.getString(0).equals("url_base"))    urlservidor = cadena_p0.getString(1);
                if(cadena_p0.getString(0).equals("id_mesero"))   idMesero = cadena_p0.getString(1);
                if(cadena_p0.getString(0).equals("id_sucursal")) idSucursal = cadena_p0.getString(1);
                cadena_p0.moveToNext();
            }
            cadena_p0.close();
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Error para obtener datos de conexion",Toast.LENGTH_SHORT).show();
            System.out.println(e);
        }

//este try carga las strings de categorias en una lista y los recnos en otra lista
        try {
            Cursor catcursor = bd.rawQuery("select recno, nomcategoria from categoria where nivel=0 order by id_categoria asc", null);
            catcursor.moveToFirst();
            //listaCategorias.add("Promociones");
            //arregloCategorias.add();
            for(int a=0; a<catcursor.getCount(); a++) {
                listaCategorias.add(catcursor.getString(catcursor.getColumnIndex("nomcategoria")));
                arregloCategorias.add(catcursor.getInt(catcursor.getColumnIndex("recno")));
                mapaCategorias.put(arregloCategorias.get(a),new categoria(arregloCategorias.get(a),listaCategorias.get(a)));
                catcursor.moveToNext();
            }
            catcursor.close();
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Error al obtener categorias de menu",Toast.LENGTH_SHORT).show();
            System.out.println(e);
        }

        try {
            Cursor platocursor = bd.rawQuery("select plato.recno, plato.nombre_plato, categoria.recno from plato,categoria where plato.id_categoria=categoria.id_categoria", null);
            platocursor.moveToFirst();
            for(int a=0; a<platocursor.getCount(); a++) {
                //Cursor opcioncursor = bd.rawQuery("SELECT ",null);

                mapaCategorias.get(platocursor.getInt(2)).getPlatosEnEstaCategoria().add(new plato(platocursor.getInt(0),platocursor.getString(1),platocursor.getInt(2)));
                mapaPlatos.put(platocursor.getInt(0),new plato(platocursor.getInt(0),platocursor.getString(1),platocursor.getInt(2)));

                platocursor.moveToNext();
                //opcioncursor.close();
            }
            platocursor.close();
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(),"exception en query llenar platos",Toast.LENGTH_SHORT).show();
            System.out.println(e);
        }

        //cierra admin y conexion a base
        bd.close();
        admin.close();
    }

    //metodo de respond hace override del metodo de la interface implementada "communicator". sirve para recibir el plato seleccionado desde un fragment, que declara un objeto de clase comm y hace referencia a la instancia de esta clase "AdminComanda"
    @Override
    public void respond(int data, int data2) {
        Bundle bundle = new Bundle();
        bundle.putInt("recnoPlato",mapaCategorias.get(arregloCategorias.get(categoriaSeleccionada)).getPlatosEnEstaCategoria().get(data).getRecno());
        bundle.putString("nombrePlato",mapaPlatos.get(mapaCategorias.get(arregloCategorias.get(categoriaSeleccionada)).getPlatosEnEstaCategoria().get(data).getRecno()).getNombre_plato());


        FragmentTransaction transaction = manager.beginTransaction();
        frag = (fragmentComandaPlatillos) manager.findFragmentByTag("fragplatillos");
        transaction.remove(frag);
        frag2.setArguments(bundle);
        transaction.add(R.id.relativefragment, frag2, "fragopciones");

        transaction.commit();
        pestanaAbierta = 0;
        listaCat.getChildAt(categoriaSeleccionada).setBackgroundColor(0x000000);
        pestanaPlatoAbierta = 1;


    }

    @Override
    public void respond2(int data, ArrayList<Integer> opciones, String coment) {
        FragmentTransaction transaction = manager.beginTransaction();
        frag2 = (fragmentComandaOpciones) manager.findFragmentByTag("fragopciones");
        transaction.remove(frag2);

        transaction.commit();
        pestanaAbierta = 0;
        pestanaPlatoAbierta = 0;

        //anadirPlatoComanda(clienteCreacionUI,clienteCreacion,getApplicationContext(),mapaCategorias.get(arregloCategorias.get(categoriaSeleccionada)).getPlatosEnEstaCategoria().get(data).getRecno(), -1 ,-1,personaSeleccionada, 0, "");
        anadirPlatoComanda(clienteCreacionUI,clienteCreacion,getApplicationContext(),data, -1 ,-1,personaSeleccionada, 0, coment, opciones);
        comandaClickFlag=true;
        hideSoftKeyboard(adminComanda.this);
    }

    @Override
    public void respond3() {
        FragmentTransaction transaction = manager.beginTransaction();
        frag2 = (fragmentComandaOpciones) manager.findFragmentByTag("fragopciones");
        transaction.remove(frag2);
        transaction.add(R.id.relativefragment, frag, "fragplatillos");

        transaction.commit();
        pestanaAbierta = 1;
        pestanaPlatoAbierta = 0;
        hideSoftKeyboard(adminComanda.this);
    }

    public void respond4(){
        FragmentTransaction transaction = manager.beginTransaction();
        frag = (fragmentComandaPlatillos) manager.findFragmentByTag("fragplatillos");
        transaction.remove(frag);
        transaction.commit();
        pestanaAbierta=0;
        listaCat.getChildAt(categoriaSeleccionada).setBackgroundColor(0x00009999);
    }

    public void cleanup(){
        mapaCategorias.clear();
        mapaPlatos.clear();
        listaCategorias.clear();
        arregloCategorias.clear();
    }

    public void setPuntos(int a) {
        puntosSuspensivos=a;
    }

    public int getPuntos(){
        return puntosSuspensivos;
    }

    private class cargarComanda extends AsyncTask<String, String, Integer>{
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(adminComanda.this);
            pDialog.setCancelable(false);
            pDialog.setMessage(getString(R.string.downloading_wait));
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        obtenerComandayparse(idCadena, idSucursal, numeroMesa);
                    }catch (JSONException e) {
                        System.out.println(e);
                        Toast.makeText(getApplicationContext(), "Error en parsing de json", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
                    if(clienteCreacion.size()==0){
                        anadirPersonaComanda(ll, clienteCreacionUI, clienteCreacion, true, 3, "M");
                        clienteCreacionUI.get(0).seleccionarPersonaUI();
                        textoCargando.setVisibility(View.INVISIBLE);
                        textoMensaje.setVisibility(View.VISIBLE);
                    }else{
                        textoCargando.setVisibility(View.VISIBLE);
                        textoMensaje.setVisibility(View.INVISIBLE);
                    }
            pDialog.dismiss();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void mostrarEnviado(){
        rlenv.setVisibility(View.VISIBLE);
        rlcrea.setVisibility(View.GONE);
    }

    public void mostrarCreacion(){
        rlenv.setVisibility(View.GONE);
        rlcrea.setVisibility(View.VISIBLE);
    }
    private class enviarComanda extends AsyncTask<String, String, Integer>{
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(adminComanda.this);
            pDialog.setCancelable(false);
            pDialog.setMessage("Enviando comanda, espere...");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(comandaClickFlag) {
                        comandaClickFlag=false;
                        while(true){
                            if(clienteCreacion.size()!=0 && clienteCreacion.get(clienteCreacion.size()-1).getListaPlatillos().size()==0){
                                eliminarPersonaComanda(ll, clienteCreacionUI,clienteCreacion,clienteCreacion.size()-1);
                            }else{
                                break;
                            }
                        }
                        if(clienteCreacion.size()!=0) {
                            String stringParaPost = "";
                            try {
                                stringParaPost = comandaaxml();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            httpHandler handler = new httpHandler();
                            handler.params.add(new BasicNameValuePair("p0", idCadena));
                            handler.params.add(new BasicNameValuePair("p1", stringParaPost));
                            String resultadopost = handler.post(urlservidor + "grabar_comanda.php");
                            //resultadopost = resultadopost.trim();
                            if(Integer.parseInt(resultadopost)>0){
                                Toast.makeText(getApplicationContext(), "Comanda insertada exitosamente. Comanda N° " + resultadopost, Toast.LENGTH_SHORT).show();
                                new cargarComanda().execute();
                                while(clienteCreacion.size()!=0) eliminarPersonaComanda(ll, clienteCreacionUI,clienteCreacion,clienteCreacion.size()-1);
                                for(int a=0; a<(numeroPersonas==0? 1:numeroPersonas); a++) {
                                    anadirPersonaComanda(ll, clienteCreacionUI, clienteCreacion, true, 3, "M");
                                }
                                clienteCreacionUI.get(0).seleccionarPersonaUI();
                                personaSeleccionada=0;
                            }else{
                                Toast.makeText(getApplicationContext(), "Ha habido un error en el envio de la comanda, intente nuevamente", Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(), "Respuesta: " + resultadopost, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            for(int a=0; a<(numeroPersonas==0?1:numeroPersonas); a++) {
                                anadirPersonaComanda(ll, clienteCreacionUI, clienteCreacion, true, numeroPersonas==0? 3:clienteEnviado.get(a).getRangoEdad(), numeroPersonas==0? "M":clienteEnviado.get(a).getGenero());
                            }
                            Toast.makeText(getApplicationContext(),"No puedes enviar una comanda vacia!",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"No puedes enviar una comanda vacia!",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
            pDialog.dismiss();
        }
    }

    public boolean revisarplatosentregados(){
        for(int a=0; a<clienteEnviado.size();a++){
            for(int b=0; b<clienteEnviado.get(a).getListaPlatillos().size();b++){
                if(clienteEnviado.get(a).getListaPlatillos().get(b).getEntregado()!=1){
                    return false;
                }
            }
        }
        return true;
    }

    /*private class reportePlato extends AsyncTask<Integer, String, Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "testing entrega plato...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Integer doInBackground(Integer... parametros) {
            String respuesta="";
            httpHandler llamadaEntregado = new httpHandler();
            llamadaEntregado.params.add(new BasicNameValuePair("p0", idCadena));
            llamadaEntregado.params.add(new BasicNameValuePair("p1", parametros[0].toString()));

            if(parametros[1]==0){
                respuesta = llamadaEntregado.post(urlservidor + "entregar_plato.php");
                clienteEnviado.get(parametros[2]).getListaPlatillos().get(parametros[3]).setEntregado(1);
                onProgressUpdate(respuesta);
            }else{
                if(clienteEnviado.get(parametros[2]).getListaPlatillos().get(parametros[3]).getEntregado()==1){
                    respuesta = llamadaEntregado.post(urlservidor + "devolver_plato.php");
                    clienteEnviado.get(parametros[2]).getListaPlatillos().get(parametros[3]).setEntregado(0);
                    //Toast.makeText(getApplicationContext(), "Devolución Exitosa " + respuesta, Toast.LENGTH_SHORT).show();
                }
            }
            return Integer.parseInt(respuesta);
        }

        @Override
        protected void onPostExecute(Integer resp) {
            super.onPostExecute(resp);
            Toast.makeText(getApplicationContext(), "terminado proceso " + resp, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //Toast.makeText(getApplicationContext(), "Entrega Exitosa " + values[0], Toast.LENGTH_SHORT).show();
        }
    }*/

}