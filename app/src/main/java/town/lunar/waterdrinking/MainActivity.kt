package town.lunar.waterdrinking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

class MainActivity : AppCompatActivity() {
    private val sipViewModel: SipViewModel by viewModels {
        WordViewModelFactory((application as WaterDrinkingApp).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = SipListAdapter { sip -> sipViewModel.delete(sip) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.delete_all_sips).setOnClickListener {
            sipViewModel.deleteAll()
        }
        sipViewModel.allWords.observe(this, Observer { sips ->
            // Update the cached copy of the words in the adapter.
            sips?.let { adapter.submitList(it) }
        })

        val addSip = findViewById<Button>(R.id.add_sip)
        addSip.setOnClickListener {
            sipViewModel.insert(Sip(Instant.now()))
        }
    }
}

class SipListAdapter(val onClick: (Sip) -> Unit) : ListAdapter<Sip, SipListAdapter.ViewHolder>(SipComparator()) {
    val dateTimeFormatter = ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC))
    class ViewHolder(val onClick: (Sip) -> Unit, view: View) : RecyclerView.ViewHolder(view) {
        val butt: Button
        val lbl: TextView
        var sip: Sip? = null

        init {
            butt = view.findViewById(R.id.delete_sip_button)
            lbl = view.findViewById(R.id.timestamp_text)
            butt.setOnClickListener {
                sip?.let {
                    onClick(it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sip_log_item, parent, false)

        return ViewHolder(onClick, view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val cur = getItem(position)
        viewHolder.sip = cur
        viewHolder.lbl.text = dateTimeFormatter.format(cur.ts)
    }

    class SipComparator : DiffUtil.ItemCallback<Sip>() {
        override fun areItemsTheSame(oldItem: Sip, newItem: Sip): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Sip, newItem: Sip): Boolean {
            return oldItem.id == newItem.id
        }
    }

}
