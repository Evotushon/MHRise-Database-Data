package com.gatheringhallstudios.mhworlddatabase.features.weapons.list

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.gatheringhallstudios.mhworlddatabase.R
import com.gatheringhallstudios.mhworlddatabase.adapters.common.BasicListDelegationAdapter
import com.gatheringhallstudios.mhworlddatabase.assets.AssetLoader
import com.gatheringhallstudios.mhworlddatabase.common.RecyclerViewFragment
import com.gatheringhallstudios.mhworlddatabase.components.DashedDividerDrawable
import com.gatheringhallstudios.mhworlddatabase.components.StandardDivider
import com.gatheringhallstudios.mhworlddatabase.data.types.WeaponType
import com.gatheringhallstudios.mhworlddatabase.features.weapons.WeaponTreeAdapter
import com.gatheringhallstudios.mhworlddatabase.features.weapons.WeaponTreeListAdapterDelegate
import com.gatheringhallstudios.mhworlddatabase.getRouter
import com.gatheringhallstudios.mhworlddatabase.setActivityTitle

/**
 * Fragment that displays the WeaponTreeCollection object.
 * This displays the weapons of a particular weapon type as a tree.
 */
class WeaponTreeListFragment : RecyclerViewFragment() {
    companion object {
        const val ARG_WEAPON_TREE_TYPE = "WEAPON_TREE_TYPE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(WeaponTreeListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val type = arguments?.getSerializable(ARG_WEAPON_TREE_TYPE) as WeaponType

        setActivityTitle(AssetLoader.getNameFor(type))

        val adapter = WeaponTreeAdapter {
            getRouter().navigateWeaponDetail(it.id)
        }
        setAdapter(adapter)

        recyclerView.addItemDecoration(StandardDivider(DashedDividerDrawable(context!!)))

        // Load data
        viewModel.setWeaponType(type)

        viewModel.nodeListData.observe(this, Observer {
            adapter.setItems(it ?: emptyList())
            adapter.notifyDataSetChanged()
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_weapon_tree, menu)
    }

    /**
     * Handled when a menu item is clicked. True is returned if handled.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.weapon_final_toggle -> {
                val newSetting = !item.isChecked
                viewModel.setShowFinal(newSetting)
                item.isChecked = newSetting
                true
            }

            // fallback to parent behavior if unhandled
            else -> super.onOptionsItemSelected(item)
        }
    }
}