package com.example.cooperadora_escuela.ui;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cooperadora_escuela.R;
import com.example.cooperadora_escuela.ui.ProductoAdapter;
import com.example.cooperadora_escuela.models.Produ;
import com.example.cooperadora_escuela.network.UserService;
import com.example.cooperadora_escuela.network.auth.ApiUser;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProducActivity extends AppCompatActivity {

    private RecyclerView recyclerProductos;
    private ProductoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produc);

        recyclerProductos = findViewById(R.id.recyclerProductos);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));

        cargarProductos();
    }

    private void cargarProductos() {
        UserService apiService = ApiUser.getRetrofit(this).create(UserService.class);
        Call<List<Produ>> call = apiService.getProductos();

        call.enqueue(new Callback<List<Produ>>() {
            @Override
            public void onResponse(Call<List<Produ>> call, Response<List<Produ>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Produ> productos = response.body();
                    adapter = new ProductoAdapter(ProducActivity.this, productos);
                    recyclerProductos.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Produ>> call, Throwable t) {
                t.printStackTrace();
                // Acá podrías mostrar un Toast de error
            }
        });
    }
}
