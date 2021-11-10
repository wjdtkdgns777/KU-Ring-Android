package com.ku_stacks.ku_ring.ui.search.fragment_staff

import androidx.recyclerview.widget.RecyclerView
import com.ku_stacks.ku_ring.data.websocket.response.SearchStaffResponse
import com.ku_stacks.ku_ring.databinding.ItemStaffBinding

class SearchStaffViewHolder(
    private val binding: ItemStaffBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(staff: SearchStaffResponse) {
        binding.staffNameTxt.text = staff.name
        binding.staffDepartmentAndCollegeTxt.text = "${staff.department} · ${staff.college}"
        binding.executePendingBindings()
    }
}